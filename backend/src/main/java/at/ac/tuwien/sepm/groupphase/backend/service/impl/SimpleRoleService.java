package at.ac.tuwien.sepm.groupphase.backend.service.impl;

import at.ac.tuwien.sepm.groupphase.backend.entity.ApplicationUser;
import at.ac.tuwien.sepm.groupphase.backend.entity.Role;
import at.ac.tuwien.sepm.groupphase.backend.exception.NotFoundException2;
import at.ac.tuwien.sepm.groupphase.backend.exception.ValidationException;
import at.ac.tuwien.sepm.groupphase.backend.repository.RoleRepository;
import at.ac.tuwien.sepm.groupphase.backend.service.RoleService;
import at.ac.tuwien.sepm.groupphase.backend.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Locale;
import java.util.Optional;

@Slf4j
@Service
public class SimpleRoleService implements RoleService {

    private final RoleRepository roleRepository;
    private final UserService userService;

    @Autowired
    public SimpleRoleService(RoleRepository roleRepository, UserService userService) {
        this.roleRepository = roleRepository;
        this.userService = userService;
    }

    @Override
    public Role create(Role role) throws ValidationException {
        if (role.getId() != null) {
            throw new ValidationException("Id must be null!");
        }
        if (!role.getName().matches("[a-zA-Z0-9]*")) {
            throw new ValidationException("Name must not contain characters other than A-Z and 0-9.");
        }
        if (role.getName().length() < 3 || role.getName().length() > 15) {
            throw new ValidationException("Name must be at least 3 and at most 15 characters long.");
        }
        for (ApplicationUser user : role.getApplicationUsers()) {
            if (!userService.userExistsByEmail(user.getEmail())) {
                throw new ValidationException("User " + user.getEmail() + "does not exist in the data base.");
            }
        }
        Role newRole = new Role(null, role.getName().toUpperCase(Locale.ROOT), null);
        newRole = roleRepository.save(newRole);
        newRole.setApplicationUsers(role.getApplicationUsers());
        return this.roleRepository.save(newRole);
    }

    @Override
    public Role findRoleByName(String name) throws NotFoundException2 {
        String uCaseName = name.toUpperCase(Locale.ROOT);
        Optional<Role> role = this.roleRepository.findRoleByName(uCaseName);
        if (role.isPresent()) {
            return role.get();
        } else {
            throw new NotFoundException2("Role not found.");
        }
    }

    @Override
    public boolean roleExistsById(Long id) {
        Optional<Role> role = roleRepository.findRoleById(id);
        return role.isPresent();
    }

    @Override
    public void deleteById(Long id) throws NotFoundException2, ValidationException {
        if (roleExistsById(id)) {
            Role role = this.getById(id);
            List<ApplicationUser> userList = this.userService.getApplicationUserByRoleId(id);
            for (ApplicationUser user : userList) {
                user.getRoles().remove(role);
                try {
                    this.userService.update(user);
                } catch (ValidationException e) {
                    throw new ValidationException("Error deleting role from user " + user.getId() + ". " + e.getMessage(),e.getCause());
                }
            }

            this.roleRepository.deleteById(id);
        } else {
            throw new NotFoundException2("Role cannot be found.");
        }
    }

    @Override
    public List<Role> findAll() {
        log.debug("Find all roles");
        return roleRepository.findAll();
    }

    @Override
    public Role getById(Long id) throws NotFoundException2 {
        Optional<Role> role = roleRepository.findRoleById(id);
        if (role.isPresent()) {
            return role.get();
        } else {
            throw new NotFoundException2("Role with ID " + id + "does not exist.");
        }
    }

    @Override
    public boolean roleExistsByName(String name) {
        String uCaseName = name.toUpperCase(Locale.ROOT);
        Optional<Role> role = roleRepository.findRoleByName(uCaseName);
        return role.isPresent();
    }

    /*
     @Override
     public List<Role> findRolesByUser(ApplicationUser applicationUser) {
     return roleRepository.findRolesByApplicationUsersId(applicationUser.getId());
     }

     @Override
    public void deleteByName(String name) throws NotFoundException2, ValidationException {
        if (roleExistsByName(name)) {
            try {
                Role role = this.findRoleByName(name);
                this.deleteById(role.getId());
            } catch (NotFoundException2 exc) {
                throw new NotFoundException2("Role cannot be found.", exc);
            } catch (ValidationException e) {
                throw e;
            }
        } else {
            throw new NotFoundException2("Role cannot be found.");
        }
    }
    */

}
