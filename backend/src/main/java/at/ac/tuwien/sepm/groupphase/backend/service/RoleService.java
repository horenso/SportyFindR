package at.ac.tuwien.sepm.groupphase.backend.service;

import at.ac.tuwien.sepm.groupphase.backend.entity.ApplicationUser;
import at.ac.tuwien.sepm.groupphase.backend.entity.Role;
import at.ac.tuwien.sepm.groupphase.backend.exception.NotFoundException2;
import at.ac.tuwien.sepm.groupphase.backend.exception.ValidationException;

import java.util.List;

public interface RoleService {

    /**
     * Creates a new role
     * @param role to create
     * @return created Role
     * @throws ValidationException if the role is not valid
     */
    Role create(Role role) throws ValidationException;

    /**
     * find role by (unique) role name
     * @param name Role Name to find
     * @return found role
     */
    Role findRoleByName(String name) throws NotFoundException2;

    /**
     * determines if a role exists by name
     * @param name Role Name to find
     * @return true if role exists
     */
    boolean roleExistsByName(String name);

    /**
     * determine if a role exists by id
     * @param id Role ID to find
     * @return true if role exists
     */
    boolean roleExistsById(Long id);

    /**
     * deletes a Role by id
     * @param id of the role to delete
     * @throws NotFoundException2 if role does not exist
     */
    void deleteById(Long id) throws NotFoundException2;

    /**
     * deletes a Role by Name
     * @param name of the role to delete
     * @throws NotFoundException2 if role does not exist
     */
    void deleteByName(String name) throws NotFoundException2;

    /**
     * find all roles
     * @return List of Roles
     */
    List<Role> findAll();

    /**
     * finds all roles associated with the provided user
     * @param applicationUser user to search for
     * @return list of roles associated with the user
     */
    List<Role> findRolesByUser(ApplicationUser applicationUser);
}
