package at.ac.tuwien.sepm.groupphase.backend.service.impl;

import at.ac.tuwien.sepm.groupphase.backend.entity.Role;
import at.ac.tuwien.sepm.groupphase.backend.exception.NotFoundException2;
import at.ac.tuwien.sepm.groupphase.backend.exception.ValidationException;
import at.ac.tuwien.sepm.groupphase.backend.repository.RoleRepository;
import at.ac.tuwien.sepm.groupphase.backend.service.RoleService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.lang.invoke.MethodHandles;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

@Service
public class SimpleRoleService implements RoleService {

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    private final RoleRepository roleRepository;

    @Autowired
    public SimpleRoleService(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    @Override
    public Role create(Role role) throws ValidationException {
        Role newRole = new Role(null, role.getName().toUpperCase(Locale.ROOT), null);
        if (false) {
            // ToDo: sanity check name (only A-Za-z0-9)
            // ToDo: sanity check users (only existing users)
            throw new ValidationException("Role invalid");
        }
        newRole = roleRepository.save(newRole);
        newRole.setApplicationUsers(role.getApplicationUsers());
        return this.roleRepository.save(newRole);
    }

    @Override
    public Role findRoleByName(String name) throws NotFoundException2 {
        Optional<Role> role = this.roleRepository.findRoleByName(name);
        if (role.isPresent()) {
            return role.get();
        } else {
            throw new NotFoundException2("Role not found.");
        }
    }

    @Override
    public boolean roleExistsByName(String name) {
        Optional<Role> role = roleRepository.findRoleByName(name);
        return role.isPresent();
    }

    @Override
    public boolean roleExistsById(Long id) {
        Optional<Role> role = roleRepository.findRoleById(id);
        return role.isPresent();
    }

    @Override
    public void deleteById(Long id) throws NotFoundException2 {
        if (roleExistsById(id)) {
            this.roleRepository.deleteById(id);
        } else {
            throw new NotFoundException2("Role cannot be found.");
        }
    }

    @Override
    public void deleteByName(String name) throws NotFoundException2 {
        if (roleExistsByName(name)) {
            try {
                Role role = this.findRoleByName(name);
                this.deleteById(role.getId());
            } catch (NotFoundException2 exc) {
                throw new NotFoundException2("Role cannot be found.", exc);
            }
        } else {
            throw new NotFoundException2("Role cannot be found.");
        }
    }

    @Override
    public List<Role> findAll() {
        LOGGER.debug("Find all roles");
        return roleRepository.findAll();
    }
}
