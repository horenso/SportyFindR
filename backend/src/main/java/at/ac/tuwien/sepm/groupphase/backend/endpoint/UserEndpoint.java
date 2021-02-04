package at.ac.tuwien.sepm.groupphase.backend.endpoint;

import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.NewUserDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.SimpleUserDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.UserDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.mapper.SimpleUserMapper;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.mapper.UserMapper;
import at.ac.tuwien.sepm.groupphase.backend.entity.ApplicationUser;
import at.ac.tuwien.sepm.groupphase.backend.entity.Role;
import at.ac.tuwien.sepm.groupphase.backend.exception.NotFoundException;
import at.ac.tuwien.sepm.groupphase.backend.exception.ValidationException;
import at.ac.tuwien.sepm.groupphase.backend.service.RoleService;
import at.ac.tuwien.sepm.groupphase.backend.service.UserService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.Authorization;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.service.spi.ServiceException;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.Valid;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Slf4j
@RestController
@CrossOrigin
@RequestMapping(value = "/api/v1/users")
@RequiredArgsConstructor
public class UserEndpoint {
    private final UserService userService;
    private final UserMapper userMapper;
    private final SimpleUserMapper simpleUserMapper;
    private final RoleService roleService;


    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    @ApiOperation(value = "Create a new user", authorizations = {@Authorization(value = "apiKey")})
    public UserDto create(@Valid @RequestBody NewUserDto newUserDto) {
        log.info("POST /api/v1/users body: {}", newUserDto);
        try {
            ApplicationUser newUser = userMapper.userDtoToApplicationUser(newUserDto);
            newUser.setRoles(this.enrichRoleSet(newUser.getRoles()));
            return userMapper.applicationUserToUserDto(userService.createApplicationUser(newUser));
        } catch (ServiceException | ValidationException e) {
            log.error(HttpStatus.BAD_REQUEST + " " + e.getMessage());
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        } catch (NotFoundException e) {
            log.error(HttpStatus.UNPROCESSABLE_ENTITY + " " + e.getMessage());
            throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, e.getMessage());
        }
    }

    @Secured({"ROLE_ADMIN", "ROLE_USER"})
    @ResponseStatus(HttpStatus.OK)
    @PutMapping
    @ApiOperation(value = "Update a user", authorizations = {@Authorization(value = "apiKey")})
    public UserDto update(@Valid @RequestBody UserDto userDto) {
        log.info("PUT /api/v1/users body: {}:", userDto);
        try {
            ApplicationUser updatedUser = userMapper.userDtoToApplicationUser(userDto);
            updatedUser.setRoles(this.enrichRoleSet(updatedUser.getRoles()));
            UserDto updatedUserDto = userMapper.applicationUserToUserDto(
                userService.update(updatedUser)
            );
            log.info("{}", updatedUser);
            return updatedUserDto;
        } catch (ValidationException e) {
            log.error(HttpStatus.NOT_FOUND + " " + e.getMessage());
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        } catch (NotFoundException e) {
            log.error(HttpStatus.UNPROCESSABLE_ENTITY + " " + e.getMessage());
            throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, e.getMessage());
        }
    }

    @Secured({"ROLE_ADMIN", "ROLE_USER"})
    @ResponseStatus(HttpStatus.OK)
    @DeleteMapping(value = "/{id}")
    @ApiOperation(value = "Delete user", authorizations = {@Authorization(value = "apiKey")})
    public void deleteUserById(@PathVariable("id") Long id) {
        log.info("DELETE /api/v1/users id {}", id);
        try {
            userService.deleteApplicationUserById(id);
        } catch (NotFoundException e) {
            log.error(HttpStatus.NOT_FOUND + " " + e.getMessage());
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }

    @Secured("ROLE_ADMIN")
    @ResponseStatus(HttpStatus.OK)
    @GetMapping(value = "")
    @ApiOperation(value = "Get all users", authorizations = {@Authorization(value = "apiKey")})
    public List<UserDto> getAll() {
        log.info("GET /api/v1/users");
        return userMapper.applicationUserListToUserDtoList(userService.findAll());
    }

    @Secured("ROLE_ADMIN")
    @ResponseStatus(HttpStatus.OK)
    @GetMapping(value = "/byRole/{id}")
    @ApiOperation(value = "Get users by role", authorizations = {@Authorization(value = "apiKey")})
    public List<UserDto> getUsersByRole(@PathVariable("id") Long id) {
        log.info("GET /api/v1/users/byRole/{}", id);
        try {
            return userMapper.applicationUserListToUserDtoList(userService.getApplicationUserByRoleId(id));
        } catch (NotFoundException e) {
            log.error(HttpStatus.NOT_FOUND + " " + e.getMessage());
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }

    @Secured("ROLE_ADMIN")
    @ResponseStatus(HttpStatus.OK)
    @GetMapping(value = "/{id}")
    @ApiOperation(value = "Get one user by id", authorizations = {@Authorization(value = "apiKey")})
    public UserDto getOneById(@PathVariable("id") Long id) {
        log.info("GET /api/v1/users/{}", id);
        try {
            return userMapper.applicationUserToUserDto(userService.getApplicationUserById(id));
        } catch (NotFoundException e) {
            log.error(HttpStatus.NOT_FOUND + " " + e.getMessage());
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }

    @Secured({"ROLE_ADMIN", "ROLE_USER"})
    @ResponseStatus(HttpStatus.OK)
    @GetMapping(value = "/email/{email}")
    @ApiOperation(value = "Get one user by email", authorizations = {@Authorization(value = "apiKey")})
    public UserDto getOneByEmail(@PathVariable("email") String email) {
        log.info("GET /api/v1/users/byEmail/{}", email);
        try {
            return userMapper.applicationUserToUserDto(userService.getApplicationUserByEmail(email));
        } catch (NotFoundException e) {
            log.error(HttpStatus.NOT_FOUND + " " + e.getMessage());
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }

    @ResponseStatus(HttpStatus.OK)
    @GetMapping(value = "/filter")
    @ApiOperation(value = "Get user list by name", authorizations = {@Authorization(value = "apiKey")})
    public List<SimpleUserDto> searchByName(@RequestParam(required = false) String name) {
        log.info("GET /api/v1/users/filter?name={}", name);
        return simpleUserMapper.entityToListDto(userService.searchByName(name));
    }


    private Set<Role> enrichRoleSet(Set<Role> roles) throws NotFoundException {
        Set<Role> returnRoles = new HashSet<>();
        for (Role role : roles) {
            if (role.getName() == null) {
                try {
                    returnRoles.add(this.roleService.getById(role.getId()));
                } catch (NotFoundException e) {
                    throw e;
                }
            } else {
                returnRoles.add(role);
            }
        }
        return returnRoles;
    }
}
