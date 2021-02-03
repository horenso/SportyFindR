package at.ac.tuwien.sepm.groupphase.backend.endpoint;

import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.DeletedSpotResponseDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.SpotDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.mapper.SpotMapper;
import at.ac.tuwien.sepm.groupphase.backend.entity.Spot;
import at.ac.tuwien.sepm.groupphase.backend.exception.NotFoundException2;
import at.ac.tuwien.sepm.groupphase.backend.exception.ServiceException;
import at.ac.tuwien.sepm.groupphase.backend.exception.ValidationException;
import at.ac.tuwien.sepm.groupphase.backend.exception.WrongUserException;
import at.ac.tuwien.sepm.groupphase.backend.service.SpotService;
import at.ac.tuwien.sepm.groupphase.backend.service.SpotSubscriptionService;
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
import java.util.ArrayList;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/api/v1/spots")
@Slf4j
public class SpotEndpoint {

    private final SpotService spotService;
    private final SpotSubscriptionService spotSubscriptionService;
    private final SpotMapper spotMapper;

    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/{id}")
    @CrossOrigin
    @ApiOperation(value = "Get one spot by id", authorizations = {@Authorization(value = "apiKey")})
    public SpotDto getOneById(@PathVariable("id") Long id) {
        log.info("GET /api/v1/spots/{}", id);
        try {
            return spotMapper.spotToSpotDto(spotService.getOneById(id));
        } catch (NotFoundException2 e) {
            log.error(HttpStatus.NOT_FOUND + " " + e.getMessage());
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }
    @Secured({"ROLE_ADMIN", "ROLE_USER"})
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    @ApiOperation(value = "Create a new spot", authorizations = {@Authorization(value = "apiKey")})
    public SpotDto create(@Valid @RequestBody SpotDto spotDto) {
        log.info("POST /api/v1/spots body: {}", spotDto);
        try {
            return spotMapper.spotToSpotDto(
                spotService.create(spotMapper.spotDtoToSpot(spotDto)));
        } catch (ValidationException | ServiceException e) {
            log.error(HttpStatus.UNPROCESSABLE_ENTITY + " " + e.getMessage());
            throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, e.getMessage());
        }
    }
    @Secured({"ROLE_ADMIN", "ROLE_USER"})
    @ResponseStatus(HttpStatus.OK)
    @DeleteMapping(value = "/{id}")
    @ApiOperation(value = "Delete a spot", authorizations = {@Authorization(value = "apiKey")})
    public DeletedSpotResponseDto delete(@PathVariable("id") Long id) {
        log.info("DELETE /api/v1/spots/{}", id);
        try {
            return DeletedSpotResponseDto.builder().deletedLocation(spotService.deleteById(id)).build();
        } catch (ServiceException | ValidationException e) {
            log.error(HttpStatus.NOT_FOUND + " " + e.getMessage());
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        } catch (WrongUserException e) {
            log.error(HttpStatus.FORBIDDEN + " " + e.getMessage());
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, e.getMessage());
        }
    }
    @Secured({"ROLE_ADMIN", "ROLE_USER"})
    @ResponseStatus(HttpStatus.OK)
    @PutMapping
    @ApiOperation(value = "Update a spot", authorizations = {@Authorization(value = "apiKey")})
    public SpotDto update(@Valid @RequestBody SpotDto spotDto) {
        log.info("PUT /api/v1/spots body: {}", spotDto);
        try {
            SpotDto updated = spotMapper.spotToSpotDto(
                spotService.update(spotMapper.spotDtoToSpot(spotDto)));
            log.info("{}", updated);
            return updated;
        } catch (ValidationException e) {
            log.error(HttpStatus.UNPROCESSABLE_ENTITY + " " + e.getMessage());
            throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, e.getMessage());
        } catch (WrongUserException e) {
            log.error(HttpStatus.FORBIDDEN + " " + e.getMessage());
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, e.getMessage());
        } catch (NotFoundException2 e) {
            log.error(HttpStatus.NOT_FOUND + " " + e.getMessage());
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    @CrossOrigin
    @ApiOperation(value = "Get list of spots for a specific location", authorizations = {@Authorization(value = "apiKey")})
    public List<SpotDto> getSpotsByLocation(@RequestParam(name = "location") Long locationId,
                                            @RequestParam(required = false, name = "hashtag") String hashtagName) {
        log.info("GET /api/v1/spots?location={}&hashtag={}", locationId,hashtagName);
        try {
            List<Spot> spots = spotService.getSpotsByLocation(locationId,hashtagName);
            List<SpotDto> spotDtos = new ArrayList<>();
            spots.forEach(spot -> spotDtos.add(spotMapper.spotToSpotDto(spot)));
            return spotDtos;
        }catch (ValidationException e){
            log.error(HttpStatus.BAD_REQUEST + " " + e.getMessage());
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }

    @ApiOperation(value = "Subscribe to the Server Sent Emitter", authorizations = {@Authorization(value = "apiKey")})
    @GetMapping(value = "/subscribe")
    @CrossOrigin
    public SseEmitter subscribeToSpot(@RequestParam Long spotId) {
        log.info("GET /api/v1/spots/subscribe with id = {}", spotId);
        return spotSubscriptionService.subscribe(spotId);
    }

}
