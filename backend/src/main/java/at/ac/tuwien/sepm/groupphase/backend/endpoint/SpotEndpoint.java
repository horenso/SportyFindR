package at.ac.tuwien.sepm.groupphase.backend.endpoint;

import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.SpotDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.mapper.SpotMapper;

import at.ac.tuwien.sepm.groupphase.backend.exception.ServiceException;
import at.ac.tuwien.sepm.groupphase.backend.exception.NotFoundException;
import at.ac.tuwien.sepm.groupphase.backend.exception.NotFoundInDatabaseException;
import at.ac.tuwien.sepm.groupphase.backend.service.SpotService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.Authorization;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.Valid;
import java.lang.invoke.MethodHandles;

@RestController
@RequestMapping(value = "/api/v1/spots")
public class SpotEndpoint {

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    private final SpotService spotService;
    private final SpotMapper spotMapper;

    @Autowired
    public SpotEndpoint(SpotService spotService, SpotMapper spotMapper) {
        this.spotService = spotService;
        this.spotMapper = spotMapper;
    }

    @Secured("ROLE_ADMIN")
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    @ApiOperation(value = "Create a new spot", authorizations = {@Authorization(value = "apiKey")})
    public SpotDto create(@Valid @RequestBody SpotDto spotDto) {
        LOGGER.info("POST /api/v1/spots body: {}", spotDto);
        try {
            return spotMapper.spotToSpotDto(
                spotService.create(spotMapper.spotDtoToSpot(spotDto)));
        } catch (ServiceException e) {
            LOGGER.error(HttpStatus.BAD_REQUEST + " " + e.getMessage());
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }


    @Secured("ROLE_ADMIN")
    @ResponseStatus(HttpStatus.OK)
    @DeleteMapping(value = "/{id}")
    @ApiOperation(value = "Create a new spot", authorizations = {@Authorization(value = "apiKey")})
    public void delete(@PathVariable("id") Long id) {
        LOGGER.info("DELETE /api/v1/spots id: {}", id);
        try {
            spotService.deleteById(id);
        } catch (NotFoundInDatabaseException e) {
            LOGGER.error(HttpStatus.NOT_FOUND + " " + e.getMessage());
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }

    @Secured("ROLE_ADMIN")
    @ResponseStatus(HttpStatus.OK)
    @PutMapping
    @ApiOperation(value = "Update an spot", authorizations = {@Authorization(value = "apiKey")})
    public SpotDto update(@Valid @RequestBody SpotDto spotDto) {
        LOGGER.info("PUT /api/v1/spots body: {}", spotDto);
        try {
            return spotMapper.spotToSpotDto(
                spotService.update(spotMapper.spotDtoToSpot(spotDto)));
        } catch (ServiceException e) {
            LOGGER.error(HttpStatus.BAD_REQUEST + " " + e.getMessage());
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }
}
