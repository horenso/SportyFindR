package at.ac.tuwien.sepm.groupphase.backend.endpoint;

import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.UserDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.mapper.UserMapper;
import at.ac.tuwien.sepm.groupphase.backend.exception.NotFoundException2;
import at.ac.tuwien.sepm.groupphase.backend.exception.ValidationException;
import at.ac.tuwien.sepm.groupphase.backend.service.UserService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.Authorization;
import org.hibernate.service.spi.ServiceException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.Valid;
import java.lang.invoke.MethodHandles;
import java.util.List;

@RestController
@RequestMapping(value = "/api/v1/users")
public class UserEndpoint {
    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    private final UserService userService;
    private final UserMapper userMapper;

    @Autowired
    public UserEndpoint(UserService userService, UserMapper userMapper) {
        this.userService = userService;
        this.userMapper = userMapper;
    }

    @Secured("ROLE_ADMIN")
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    @ApiOperation(value = "Create a new user", authorizations = {@Authorization(value = "apiKey")})
    public UserDto create(@Valid @RequestBody UserDto userDto) {
        LOGGER.info("POST /api/v1/users body: {}", userDto);
        try {
            return userMapper.applicationUserToUserDto(
                userService.createApplicationUser(userMapper.userDtoToApplicationUser(userDto)));
        } catch (ServiceException | ValidationException e) {
            LOGGER.error(HttpStatus.BAD_REQUEST + " " + e.getMessage());
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    @Secured("ROLE_ADMIN")
    @ResponseStatus(HttpStatus.OK)
    @PutMapping
    @ApiOperation(value = "Update a user", authorizations = {@Authorization(value = "apiKey")})
    public UserDto update(@Valid @RequestBody UserDto userDto) {
        LOGGER.info("POST /api/v1/users body: {}:", userDto);
        try {
            UserDto updatedUser = userMapper.applicationUserToUserDto(
                userService.update(userMapper.userDtoToApplicationUser(userDto))
            );
            LOGGER.info("{}", updatedUser);
            return updatedUser;
        } catch (NotFoundException2 e) {
            LOGGER.error(HttpStatus.NOT_FOUND + " " + e.getMessage());
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }

    @Secured("ROLE_ADMIN")
    @ResponseStatus(HttpStatus.OK)
    @DeleteMapping(value = "/{id}")
    @ApiOperation(value = "Delete user", authorizations = {@Authorization(value = "apiKey")})
    public void deleteUserById(@PathVariable("id") Long id) {
        LOGGER.info("DELETE /api/v1/users id: {}", id);
        try {
            userService.deleteApplicationUserById(id);
        } catch (NotFoundException2 e) {
            LOGGER.error(HttpStatus.NOT_FOUND + " " + e.getMessage());
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }

    @Secured("ROLE_ADMIN")
    @ResponseStatus(HttpStatus.OK)
    @GetMapping(value = "")
    @ApiOperation(value = "Get all users", authorizations = {@Authorization(value = "apiKey")})
    public List<UserDto> getAll() {
        LOGGER.info("GET /api/v1/users");
        return userMapper.applicationUserListToUserDtoList(userService.findAll());
    }

    @Secured("ROLE_ADMIN")
    @ResponseStatus(HttpStatus.OK)
    @GetMapping(value = "/byRole/{id}")
    @ApiOperation(value = "Get all users", authorizations = {@Authorization(value = "apiKey")})
    public List<UserDto> getUsersByRole(@PathVariable("id") Long id) {
        LOGGER.info("GET /api/v1/users/byRole/{}", id);
        try {
            return userMapper.applicationUserListToUserDtoList(userService.findApplicationUserByRoleId(id));
        } catch (NotFoundException2 e) {
            LOGGER.error(HttpStatus.NOT_FOUND + " " + e.getMessage());
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }

    @Secured("ROLE_ADMIN")
    @ResponseStatus(HttpStatus.OK)
    @GetMapping(value = "/{id}")
    @ApiOperation(value = "Get one user by id", authorizations = {@Authorization(value = "apiKey")})
    public UserDto getOneById(@PathVariable("id") Long id) {
        LOGGER.info("GET /api/v1/users/{}", id);
        try {
            return userMapper.applicationUserToUserDto(userService.findApplicationUserById(id));
        } catch (NotFoundException2 e) {
            LOGGER.error(HttpStatus.NOT_FOUND + " " + e.getMessage());
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }
}
