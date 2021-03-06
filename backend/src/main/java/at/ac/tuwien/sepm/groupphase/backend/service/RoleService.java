package at.ac.tuwien.sepm.groupphase.backend.service;

import at.ac.tuwien.sepm.groupphase.backend.entity.Role;
import at.ac.tuwien.sepm.groupphase.backend.exception.NotFoundException;
import at.ac.tuwien.sepm.groupphase.backend.exception.ValidationException;

import java.util.List;

public interface RoleService {

    /**
     * Creates a new role
     *
     * @param role to create
     * @return created Role
     * @throws ValidationException if the role is not valid
     */
    Role create(Role role) throws ValidationException;

    /**
     * find role by (unique) role name
     *
     * @param name Role Name to find
     * @return found role
     * @throws NotFoundException if the role can not be found
     */
    Role findRoleByName(String name) throws NotFoundException;

    /**
     * determines if a role exists by name
     *
     * @param name Role Name to find
     * @return true if role exists
     */
    boolean roleExistsByName(String name);

    /**
     * determine if a role exists by id
     *
     * @param id Role ID to find
     * @return true if role exists
     */
    boolean roleExistsById(Long id);

    /**
     * deletes a Role by id and removes the role from all users having it
     *
     * @param id of the role to delete
     * @throws NotFoundException   if role does not exist
     * @throws ValidationException if updating user does not work
     */
    void deleteById(Long id) throws NotFoundException, ValidationException;

    /**
     * find all roles
     *
     * @return List of Roles
     */
    List<Role> findAll();

    /**
     * Finds a Role by id
     *
     * @param id role to search for
     * @return Role
     * @throws NotFoundException if role does not exist
     */
    Role getById(Long id) throws NotFoundException;
}
