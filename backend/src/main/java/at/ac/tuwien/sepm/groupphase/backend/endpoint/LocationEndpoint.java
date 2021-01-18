package at.ac.tuwien.sepm.groupphase.backend.endpoint;

import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.LocationDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.SpotDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.mapper.LocationMapper;
import at.ac.tuwien.sepm.groupphase.backend.entity.LocationSearchObject;
import at.ac.tuwien.sepm.groupphase.backend.exception.NotFoundException;
import at.ac.tuwien.sepm.groupphase.backend.exception.NotFoundException2;
import at.ac.tuwien.sepm.groupphase.backend.exception.ServiceException;
import at.ac.tuwien.sepm.groupphase.backend.exception.ValidationException;
import at.ac.tuwien.sepm.groupphase.backend.service.LocationService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.Authorization;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.Valid;
import java.lang.invoke.MethodHandles;
import java.util.List;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping(value = "/api/v1/locations")
public class LocationEndpoint {

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    private final LocationService locationService;
    private final LocationMapper locationMapper;

    @Secured("ROLE_ADMIN")
    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/{id}")
    @ApiOperation(value = "Get one location by id", authorizations = {@Authorization(value = "apiKey")})
    public LocationDto getOneById(@PathVariable("id") Long id) {
        log.info("Get /api/v1/locations/{}", id);
        try {
            return locationMapper.locationToLocationDto(locationService.getOneById(id));
        } catch (NotFoundException2 e) {
            log.error(HttpStatus.NOT_FOUND + " " + e.getMessage());
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }

    @Secured("ROLE_ADMIN")
    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    @ApiOperation(value = "Get list of locations", authorizations = {@Authorization(value = "apiKey")})
    public List<LocationDto> findAll() {
        log.info("GET /api/v1/locations");
        return locationMapper.entityToListDto((locationService.findAll()));
    }

    @Secured("ROLE_ADMIN")
    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/filter")
    @ApiOperation(value = "Filter locations by distance and categories of spots",
        authorizations = {@Authorization(value = "apiKey")})
    public List<LocationDto> filter(@RequestParam(required = false) Long categoryId,
                                    @RequestParam(required = false, defaultValue = "0") Double latitude,
                                    @RequestParam(required = false, defaultValue = "0") Double longitude,
                                    @RequestParam(required = false, defaultValue = "0") Double radius) {

        log.info("GET /api/v1/locations/filter?" +
            "categoryId=" + categoryId + "&latitude=" + latitude + "&longitude=" + longitude + "&radius=" + radius);

        LocationSearchObject locationSearchObject = new LocationSearchObject(categoryId, latitude, longitude, radius);

        try {
            return locationMapper.entityToListDto(locationService.filter(locationSearchObject));
        } catch (ServiceException | NotFoundException2 e) {
            LOGGER.error(HttpStatus.NOT_FOUND + " " + e.getMessage());
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }

    }


}
