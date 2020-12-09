package at.ac.tuwien.sepm.groupphase.backend.endpoint;

import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.MessageDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.SpotDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.mapper.SpotMapper;
import at.ac.tuwien.sepm.groupphase.backend.exception.NotFoundInDatabaseException;
import at.ac.tuwien.sepm.groupphase.backend.exception.ServiceException;
import at.ac.tuwien.sepm.groupphase.backend.exception.ValidationException;
import at.ac.tuwien.sepm.groupphase.backend.service.SpotService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.Authorization;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.couchbase.CouchbaseProperties;
import org.springframework.data.util.Pair;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import org.yaml.snakeyaml.emitter.Emitter;
import reactor.core.publisher.Flux;

import javax.validation.Valid;
import java.io.IOException;
import java.lang.invoke.MethodHandles;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

@RestController
@RequestMapping(value = "/api/v1/spots")
public class SpotEndpoint {

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    private final SpotService spotService;
    private final SpotMapper spotMapper;

    /**
     * All emitters (clients who observe so to speak) are stored in a currency save list,
     * that way a client can subscribe to particular spot and receive updates about that spot
     * regarding messages and reactions.
     */
    private Map<Long, List<SseEmitter>> emitters = new ConcurrentHashMap<>();

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
        }catch (ServiceException | ValidationException e){
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
        }catch (NotFoundInDatabaseException e){
            LOGGER.error(HttpStatus.NOT_FOUND +" "+e.getMessage());
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }

    @ApiOperation(value = "Subscribe to the Sse to receive message updates",
        authorizations = {@Authorization(value = "apiKey")})
    @GetMapping(value="/subscribe", consumes = MediaType.ALL_VALUE)
    public SseEmitter subscribeToSpot(@RequestParam Long spotId){
//        return Flux.
        SseEmitter sseEmitter = new SseEmitter(Long.MAX_VALUE);
        LOGGER.info("New client subscribed to spot {}", spotId);
        try {
            sseEmitter.send(SseEmitter.event().name("INIT"));
            LOGGER.info("SENT INIT");
        } catch (IOException e){
            LOGGER.error("Subscription error: {}", e);
        }
        if (!emitters.containsKey(spotId)) {
            emitters.put(spotId, new ArrayList<>());
        }
        emitters.get(spotId).add(sseEmitter);
        sseEmitter.onCompletion(() -> emitters.get(spotId).remove(sseEmitter));
        return sseEmitter;
    }

    public void dispatch(MessageDto message) {
        List<SseEmitter> emitterList = emitters.get(message.getSpotId());
        if (emitterList == null)
            return;
        for (var emitter : emitterList) {
            try {
                emitter.send(SseEmitter.event().name("message").data(message));
                LOGGER.info("Sent message: " + message);
            } catch (IOException e){
                emitterList.remove(emitter);
                LOGGER.info("Emitter removed");
            }
        }
    }

    // TODO: a second dispatch methods with reactions
//    public void dispatch(Long spotId, ReactionDto reaction) {
//        for (var emitter : emitters) {
//            try {
//                emitter.send(SseEmitter.event().name("newMessage").data(newMessage));
//            } catch (IOException e){
//                LOGGER.error("Sending error: {}", e);
//            }
//        }
//    }

}
