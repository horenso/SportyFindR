package at.ac.tuwien.sepm.groupphase.backend.endpoint;

import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.LocationDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.mapper.LocationMapper;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.LocationSearchObject;
import at.ac.tuwien.sepm.groupphase.backend.exception.NotFoundException2;
import at.ac.tuwien.sepm.groupphase.backend.exception.ServiceException;
import at.ac.tuwien.sepm.groupphase.backend.service.LocationService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.Authorization;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import java.util.List;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping(value = "/api/v1/locations")
public class LocationEndpoint {

    private final LocationService locationService;
    private final LocationMapper locationMapper;


    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/{id}")
    @CrossOrigin
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

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    @CrossOrigin
    @ApiOperation(value = "Filter locations by distance and categories of spots", authorizations = {@Authorization(value = "apiKey")})
    public List<LocationDto> find(@RequestParam(required = false) Long categoryLoc,
                                    @RequestParam(required = false) Double latitude,
                                    @RequestParam(required = false) Double longitude,
                                    @RequestParam(required = false, defaultValue = "0") Double radius) {

        log.info("GET /api/v1/locations/?" +
            "categoryLoc=" + categoryLoc + "&latitude=" + latitude + "&longitude=" + longitude + "&radius=" + radius);

        LocationSearchObject locationSearchObject = new LocationSearchObject(categoryLoc, latitude, longitude, radius);

        return locationMapper.entityToListDto(locationService.find(locationSearchObject));

    }


}
