package at.ac.tuwien.sepm.groupphase.backend.datagenerator;

import at.ac.tuwien.sepm.groupphase.backend.entity.ApplicationUser;
import at.ac.tuwien.sepm.groupphase.backend.entity.Role;
import at.ac.tuwien.sepm.groupphase.backend.exception.NotFoundException2;
import at.ac.tuwien.sepm.groupphase.backend.exception.ValidationException;
import at.ac.tuwien.sepm.groupphase.backend.service.RoleService;
import at.ac.tuwien.sepm.groupphase.backend.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.lang.invoke.MethodHandles;
import java.util.HashSet;

@Profile("generateDefaultLogin")
@Component
public class DefaultLoginGenerator {
    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    // Parameters
    private static final String ADMIN_ROLE_NAME = "ADMIN";
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
    private void generateAdminLogin() throws ValidationException, NotFoundException2 {
        try {
            Role adminRole = this.generateAdminRole();
            this.generateAdminUser(adminRole);
        } catch (ValidationException e) {
            throw new ValidationException(e);
        } catch (NotFoundException2 e) {
            throw new NotFoundException2(e);
        }
    }

    private Role generateAdminRole() throws ValidationException, NotFoundException2 {
        Role adminRole = new Role(null, ADMIN_ROLE_NAME, null);
        if (!roleService.roleExistsByName(ADMIN_ROLE_NAME)) {
            try {
                return roleService.create(adminRole);
            } catch (ValidationException e) {
                throw new ValidationException("Couldn't create Admin Role", e);
            }
        } else {
            System.out.println("Admin Role was already created");
            try {
                return roleService.findRoleByName(ADMIN_ROLE_NAME);
            } catch (NotFoundException2 e) {
                throw new NotFoundException2("Admin role not found", e);
            }
        }
    }

    private void generateAdminUser(Role adminRole) throws ValidationException, NotFoundException2 {
        HashSet<Role> roles = new HashSet<>();
        roles.add(adminRole);
        if (!userService.userExistsByEmail(ADMIN_EMAIL)) {
            try {
                ApplicationUser user = new ApplicationUser(null, ADMIN_USER_NAME, ADMIN_EMAIL, ADMIN_PASSWORD, true, roles);
                userService.createApplicationUser(user);
            } catch (ValidationException e) {
                throw new ValidationException("Couldn't create Admin User", e);
            }
        } else {
            System.out.println("Admin User was already created, updating admin user.");
            try {
                ApplicationUser user = userService.findApplicationUserByEmail(ADMIN_EMAIL);
                user.setName(ADMIN_USER_NAME);
                user.setEmail(ADMIN_EMAIL);
                user.setPassword(ADMIN_PASSWORD);
                user.setEnabled(true);
                user.setRoles(roles);
                userService.update(user);
            } catch (NotFoundException2 e) {
                throw new NotFoundException2("Couldn't find Admin User", e);
            }
        }
    }
}
