package at.ac.tuwien.sepm.groupphase.backend.endpoint;

import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.LocationDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.mapper.LocationMapper;
import at.ac.tuwien.sepm.groupphase.backend.exception.ServiceException;
import at.ac.tuwien.sepm.groupphase.backend.service.LocationService;
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
@RequestMapping(value = "/api/v1/locations")
public class LocationEndpoint {

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    private final LocationService locationService;
    private final LocationMapper locationMapper;

    @Autowired
    public LocationEndpoint(LocationService locationService, LocationMapper locationMapper) {
        this.locationService = locationService;
        this.locationMapper = locationMapper;
    }

    @Secured("ROLE_ADMIN")
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    @ApiOperation(value = "Create a new location", authorizations = {@Authorization(value = "apiKey")})
    public LocationDto create(@Valid @RequestBody LocationDto locationDto) {
        LOGGER.info("POST /api/v1/locations body: {}", locationDto);
        try {
            return locationMapper.locationToLocationDto(
                locationService.create(locationMapper.locationDtoToLocation(locationDto)));
        }catch (ServiceException e){
            LOGGER.error(HttpStatus.BAD_REQUEST +" "+ e.getMessage());
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

}
