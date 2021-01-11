package at.ac.tuwien.sepm.groupphase.backend.service;

import at.ac.tuwien.sepm.groupphase.backend.entity.Role;
import at.ac.tuwien.sepm.groupphase.backend.exception.NotFoundException2;

public interface RoleService {

    /**
     * Creates a new role
     * @param role to create
     * @return created Role
     */
    Role create(Role role);

    /**
     * find role by (unique) role name
     * @param name Role Name to find
     * @return found role
     */
    Role findRoleByName(String name) throws NotFoundException2;

    /**
     * determines if a role exists
     * @param name Role Name to find
     * @return true if role exists
     */
    boolean roleExistsByName(String name);

    /**
     * deletes a Role by id
     * @param id of the role to delete
     * @throws NotFoundException2
     */
    void deleteById(Long id) throws NotFoundException2;

    /**
     * deletes a Role by Name
     * @param name of the role to delete
     * @throws NotFoundException2
     */
    void deleteByName(String name) throws NotFoundException2;
}
