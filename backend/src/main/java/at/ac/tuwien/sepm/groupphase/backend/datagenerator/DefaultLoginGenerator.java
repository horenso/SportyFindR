package at.ac.tuwien.sepm.groupphase.backend.datagenerator;

import at.ac.tuwien.sepm.groupphase.backend.entity.ApplicationUser;
import at.ac.tuwien.sepm.groupphase.backend.entity.Role;
import at.ac.tuwien.sepm.groupphase.backend.exception.NotFoundException;
import at.ac.tuwien.sepm.groupphase.backend.exception.ValidationException;
import at.ac.tuwien.sepm.groupphase.backend.service.RoleService;
import at.ac.tuwien.sepm.groupphase.backend.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.HashSet;

@Slf4j
@Profile("generateDefaultLogin")
@Component
public class DefaultLoginGenerator {

    // Parameters
    private static final String ADMIN_ROLE_NAME = "ADMIN";
    private static final String USER_ROLE_NAME = "USER";
    private static final String ADMIN_USER_NAME = "defaultAdmin";
    private static final String ADMIN_EMAIL = "admin@sportyfindr.at";
    private static final String ADMIN_PASSWORD = "sp0rtiF1ndM3";

    private final UserService userService;
    private final RoleService roleService;

    public DefaultLoginGenerator(UserService userService, RoleService roleService) {
        this.userService = userService;
        this.roleService = roleService;
    }

    @PostConstruct
    private void generateAdminLogin() throws ValidationException, NotFoundException {
        try {
            Role adminRole = this.generateRole(ADMIN_ROLE_NAME);
            Role userRole = this.generateRole(USER_ROLE_NAME);

            HashSet<Role> roles = new HashSet<>();
            roles.add(adminRole);
            roles.add(userRole);

            this.generateAdminUser(roles);
        } catch (ValidationException e) {
            throw new ValidationException(e);
        } catch (NotFoundException e) {
            throw new NotFoundException(e);
        }
    }

    private Role generateRole(String roleName) throws ValidationException, NotFoundException {
        if (!roleService.roleExistsByName(roleName)) {
            try {
                Role role = Role.builder()
                    .name(roleName)
                    .build();
                return roleService.create(role);
            } catch (ValidationException e) {
                throw new ValidationException("Couldn't create Admin Role", e);
            }
        } else {
            log.info("Role " + roleName + " was already created");
            try {
                return roleService.findRoleByName(roleName);
            } catch (NotFoundException e) {
                throw new NotFoundException(roleName + " role not found", e);
            }
        }
    }

    private void generateAdminUser(HashSet<Role> roles) throws ValidationException, NotFoundException {
        if (!userService.userExistsByEmail(ADMIN_EMAIL)) {
            try {
                ApplicationUser user = ApplicationUser.builder()
                    .name(ADMIN_USER_NAME)
                    .email(ADMIN_EMAIL)
                    .password(ADMIN_PASSWORD)
                    .enabled(true)
                    .roles(roles)
                    .build();
                userService.createApplicationUser(user);
            } catch (ValidationException e) {
                throw new ValidationException("Couldn't create Admin User", e);
            }
        } else {
            log.info("Admin User was already created, updating admin user.");
            try {
                ApplicationUser user = userService.getApplicationUserByEmail(ADMIN_EMAIL);
                user.setName(ADMIN_USER_NAME);
                user.setEmail(ADMIN_EMAIL);
                user.setPassword(ADMIN_PASSWORD);
                user.setEnabled(true);
                user.setRoles(roles);
                userService.update(user);
            } catch (NotFoundException e) {
                throw new NotFoundException("Couldn't find Admin User", e);
            }
        }
    }
}
