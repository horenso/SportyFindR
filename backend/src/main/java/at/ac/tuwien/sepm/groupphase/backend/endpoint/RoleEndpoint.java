package at.ac.tuwien.sepm.groupphase.backend.endpoint;

import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.RoleDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.mapper.RoleMapper;
import at.ac.tuwien.sepm.groupphase.backend.entity.ApplicationUser;
import at.ac.tuwien.sepm.groupphase.backend.entity.Role;
import at.ac.tuwien.sepm.groupphase.backend.exception.NotFoundException2;
import at.ac.tuwien.sepm.groupphase.backend.exception.ValidationException;
import at.ac.tuwien.sepm.groupphase.backend.service.RoleService;
import at.ac.tuwien.sepm.groupphase.backend.service.UserService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.Authorization;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.catalina.User;
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
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Slf4j
@RestController
@RequestMapping(value = "/api/v1/roles")
@RequiredArgsConstructor
public class RoleEndpoint {
    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    private final RoleService roleService;
    private final RoleMapper roleMapper;
    private final UserService userService;

    @Secured("ROLE_ADMIN")
    @ResponseStatus(HttpStatus.OK)
    @GetMapping(value = "")
    @ApiOperation(value = "Get all roles", authorizations = {@Authorization(value = "apiKey")})
    public List<RoleDto> getAll() {
        log.info("GET /api/v1/roles");
        return roleMapper.roleListToRoleDtoList(roleService.findAll());
    }

    @Secured("ROLE_ADMIN")
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    @ApiOperation(value = "Create a new role", authorizations = {@Authorization(value = "apiKey")})
    public RoleDto create(@Valid @RequestBody RoleDto roleDto) {
        log.info("POST /api/v1/role body: {}", roleDto);
        try {
            return roleMapper.roleToRoleDto(
                roleService.create(roleMapper.roleDtoToRole(roleDto)));
        } catch (ServiceException | ValidationException e) {
            log.error(HttpStatus.BAD_REQUEST + " " + e.getMessage());
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    @Secured("ROLE_ADMIN")
    @ResponseStatus(HttpStatus.OK)
    @DeleteMapping(value = "/{id}")
    @ApiOperation(value = "Delete role", authorizations = {@Authorization(value = "apiKey")})
    public void deleteRoleById(@PathVariable("id") Long id) {
        log.info("DELETE /api/v1/roles id: {}", id);
        try {
            roleService.deleteById(id);
        } catch (NotFoundException2 e) {
            log.error(HttpStatus.NOT_FOUND + " " + e.getMessage());
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }

    private Set<ApplicationUser> enrichRoleSet(Set<ApplicationUser> users) throws NotFoundException2 {
        Set<ApplicationUser> returnUsers = new HashSet<>();
        for (ApplicationUser user : users) {
            if (user.getName() == null || user.getEmail() == null) {
                try {
                    returnUsers.add(this.userService.getApplicationUserById(user.getId()));
                } catch (NotFoundException2 e) {
                    throw e;
                }
            } else {
                returnUsers.add(user);
            }
        }
        return returnUsers;
    }
}
