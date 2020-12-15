package at.ac.tuwien.sepm.groupphase.backend.endpoint;

import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.SpotDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.mapper.SpotMapper;
import at.ac.tuwien.sepm.groupphase.backend.exception.NotFoundException;
import at.ac.tuwien.sepm.groupphase.backend.exception.ServiceException;
import at.ac.tuwien.sepm.groupphase.backend.exception.ValidationException;
import at.ac.tuwien.sepm.groupphase.backend.service.SpotService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.Authorization;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import javax.validation.Valid;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/api/v1/spots")
@Slf4j
public class SpotEndpoint {

    private final SpotService spotService;
    private final SpotMapper spotMapper;

    @Secured("ROLE_ADMIN")
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    @ApiOperation(value = "Create a new spot", authorizations = {@Authorization(value = "apiKey")})
    public SpotDto create(@Valid @RequestBody SpotDto spotDto) {
        log.info("POST /api/v1/spots body: {}", spotDto);
        try {
            return spotMapper.spotToSpotDto(
                spotService.create(spotMapper.spotDtoToSpot(spotDto)));
        } catch (ValidationException | ServiceException e) {
            log.error(HttpStatus.BAD_REQUEST + " " + e.getMessage());
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    @Secured("ROLE_ADMIN")
    @ResponseStatus(HttpStatus.OK)
    @DeleteMapping(value = "/{id}")
    @ApiOperation(value = "Create a new spot", authorizations = {@Authorization(value = "apiKey")})
    public void delete(@PathVariable("id") Long id) {
        log.info("DELETE /api/v1/spots id: {}", id);
        try {
            spotService.deleteById(id);
        } catch (NotFoundException | ValidationException e) {
            log.error(HttpStatus.NOT_FOUND + " " + e.getMessage());
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }

    @Secured("ROLE_ADMIN")
    @ResponseStatus(HttpStatus.OK)
    @PutMapping
    @ApiOperation(value = "Update an spot", authorizations = {@Authorization(value = "apiKey")})
    public SpotDto update(@Valid @RequestBody SpotDto spotDto) {
        log.info("PUT /api/v1/spots body: {}", spotDto);
        try {
            return spotMapper.spotToSpotDto(
                spotService.update(spotMapper.spotDtoToSpot(spotDto)));
        } catch (ServiceException e) {
            log.error(HttpStatus.BAD_REQUEST + " " + e.getMessage());
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    @ApiOperation(value = "Subscribe to the Server Sent Emitter", authorizations = {@Authorization(value = "apiKey")})
    @GetMapping(value = "/subscribe")
    @CrossOrigin
    public SseEmitter subscribeToSpot(@RequestParam Long spotId) {
        log.info("GET /api/v1/spots/subscribe with id = {}", spotId);
        return spotService.subscribe(spotId);
    }

}
