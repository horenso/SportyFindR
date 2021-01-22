package at.ac.tuwien.sepm.groupphase.backend.endpoint;

import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.RoleDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.mapper.RoleMapper;
import at.ac.tuwien.sepm.groupphase.backend.exception.NotFoundException2;
import at.ac.tuwien.sepm.groupphase.backend.exception.ValidationException;
import at.ac.tuwien.sepm.groupphase.backend.service.RoleService;
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
@RequestMapping(value = "/api/v1/roles")
public class RoleEndpoint {
    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    private final RoleService roleService;
    private final RoleMapper roleMapper;

    @Autowired
    public RoleEndpoint(RoleService roleService, RoleMapper roleMapper) {
        this.roleService = roleService;
        this.roleMapper = roleMapper;
    }

    @Secured("ROLE_ADMIN")
    @ResponseStatus(HttpStatus.OK)
    @GetMapping(value = "")
    @ApiOperation(value = "Get all roles", authorizations = {@Authorization(value = "apiKey")})
    public List<RoleDto> getAll() {
        LOGGER.info("GET /api/v1/roles");
        return roleMapper.roleListToRoleDtoList(roleService.findAll());
    }

    @Secured("ROLE_ADMIN")
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    @ApiOperation(value = "Create a new role", authorizations = {@Authorization(value = "apiKey")})
    public RoleDto create(@Valid @RequestBody RoleDto roleDto) {
        LOGGER.info("POST /api/v1/role body: {}", roleDto);
        try {
            return roleMapper.roleToRoleDto(
                roleService.create(roleMapper.roleDtoToRole(roleDto)));
        } catch (ServiceException | ValidationException e) {
            LOGGER.error(HttpStatus.BAD_REQUEST + " " + e.getMessage());
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    @Secured("ROLE_ADMIN")
    @ResponseStatus(HttpStatus.OK)
    @DeleteMapping(value = "/{id}")
    @ApiOperation(value = "Delete role", authorizations = {@Authorization(value = "apiKey")})
    public void deleteRoleById(@PathVariable("id") Long id) {
        LOGGER.info("DELETE /api/v1/roles id: {}", id);
        try {
            roleService.deleteById(id);
        } catch (NotFoundException2 e) {
            LOGGER.error(HttpStatus.NOT_FOUND + " " + e.getMessage());
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }
}
