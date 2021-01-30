package at.ac.tuwien.sepm.groupphase.backend.service;

import at.ac.tuwien.sepm.groupphase.backend.entity.ApplicationUser;
import at.ac.tuwien.sepm.groupphase.backend.exception.NotFoundException2;
import at.ac.tuwien.sepm.groupphase.backend.exception.ValidationException;
import at.ac.tuwien.sepm.groupphase.backend.exception.WrongUserException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.List;

public interface UserService extends UserDetailsService {

    /**
     * Find a user in the context of Spring Security based on the email address
     * <p>
     * For more information have a look at this tutorial:
     * https://www.baeldung.com/spring-security-authentication-with-a-database
     *
     * @param email the email address
     * @return a Spring Security user
     * @throws UsernameNotFoundException is thrown if the specified user does not exists
     */
    @Override
    UserDetails loadUserByUsername(String email) throws UsernameNotFoundException;

    /**
     * Find an application user based on the email address
     *
     * @param email the email address
     * @return an application user
     * @throws NotFoundException2 if the user does not exist
     */
    ApplicationUser getApplicationUserByEmail(String email) throws NotFoundException2;

    /**
     * Find an Application user based on the id
     * @param id user id
     * @return the application user that has been found
     * @throws NotFoundException2 if application user does not exist
     */
    ApplicationUser getApplicationUserById(Long id) throws NotFoundException2;

    /**
     * Creates a new Application UserEndpoint
     * @param applicationUser Application applicationUser
     * @return returns the applicationUser that has been created
     */
    ApplicationUser createApplicationUser(ApplicationUser applicationUser) throws ValidationException;

    /**
     * Deletes the user with id id
     * @param id of the user to delete
     * @throws NotFoundException2 if user was not found
     */
    void deleteApplicationUserById(Long id) throws NotFoundException2;

    /**
     * finds all Application Users
     * @return a List of all Application Users
     */
    List<ApplicationUser> findAll();

    /**
     * update existing Application User
     * @param user application user to update
     * @return updated ApplicationUser
     * @throws NotFoundException2 if the user was not found
     */
    ApplicationUser update(ApplicationUser user) throws NotFoundException2, ValidationException;

    /**
     * find users having to a certain role
     * @param roleId of the role to search for
     * @return List of Application Users having the role
     * @throws NotFoundException2 if the role was not found
     */
    List<ApplicationUser> getApplicationUserByRoleId(Long roleId) throws NotFoundException2;

    /**
     * check if user with email exists
     * @param email of user to search
     * @return true if user exists
     */
    boolean userExistsByEmail(String email);
}
