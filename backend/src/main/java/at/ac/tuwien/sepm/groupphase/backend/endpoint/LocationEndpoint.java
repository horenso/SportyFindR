package at.ac.tuwien.sepm.groupphase.backend.endpoint;

import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.LocationDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.mapper.LocationMapper;
import at.ac.tuwien.sepm.groupphase.backend.entity.Location;
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
import java.util.LinkedList;
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
    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    @ApiOperation(value = "Get list of locations", authorizations = {@Authorization(value = "apiKey")})
    public List<LocationDto> findAll() {
        LOGGER.info("GET /api/v1/locations");
        List<Location> locationList = this.locationService.findAll();
        List<LocationDto> locationDtoList = new LinkedList<>();

        locationList.forEach(location -> locationDtoList.add(locationMapper.locationToLocationDto(location)));
        return locationDtoList;
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
        } catch (ServiceException | ValidationException e) {
            LOGGER.error(HttpStatus.BAD_REQUEST + " " + e.getMessage());
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
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

        LOGGER.info("GET /api/v1/locations/filter?" +
            "categoryId=" + categoryId + "&latitude=" + latitude + "&longitude=" + longitude + "&radius=" + radius);

        try {
            return locationMapper.entityToListDto(locationService.filter(categoryId, latitude, longitude, radius));
        } catch (ServiceException e) {
            LOGGER.error(HttpStatus.NOT_FOUND + " " + e.getMessage());
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }

    }

}
