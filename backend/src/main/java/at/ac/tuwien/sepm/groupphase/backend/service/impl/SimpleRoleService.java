package at.ac.tuwien.sepm.groupphase.backend.service.impl;

import at.ac.tuwien.sepm.groupphase.backend.entity.Role;
import at.ac.tuwien.sepm.groupphase.backend.exception.NotFoundException2;
import at.ac.tuwien.sepm.groupphase.backend.repository.RoleRepository;
import at.ac.tuwien.sepm.groupphase.backend.service.RoleService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.lang.invoke.MethodHandles;

public class SimpleRoleService implements RoleService {

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    private final RoleRepository roleRepository;

    @Autowired
    public SimpleRoleService(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    @Override
    public Role create(Role role) {
        Role newRole = new Role(null, role.getName(), null);
        return this.roleRepository.save(newRole);
    }

    @Override
    public Role findRoleByName(String name) throws NotFoundException2 {
        if (this.roleExistsByName(name)) {
            Role role = this.roleRepository.findRoleByName(name);
            if (role == null) {
                // should only happen if between the execution of first and third line the role is deleted
                throw new NotFoundException2("Role not found.");
            } else {
                return role;
            }
        } else {
            throw new NotFoundException2("Role not found.");
        }
    }

    @Override
    public boolean roleExistsByName(String name) {
        Role role = roleRepository.findRoleByName(name);
        if (role == null) {
            return false;
        } else {
            return true;
        }
    }

    @Override
    public void deleteById(Long id) throws NotFoundException2 {
        this.roleRepository.deleteById(id);
    }

    @Override
    public void deleteByName(String name) throws NotFoundException2 {
        Role role = this.findRoleByName(name);
        this.deleteById(role.getId());
    }
}
