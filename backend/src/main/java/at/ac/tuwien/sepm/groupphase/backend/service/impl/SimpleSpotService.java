package at.ac.tuwien.sepm.groupphase.backend.service.impl;

import at.ac.tuwien.sepm.groupphase.backend.endpoint.mapper.MessageMapper;
import at.ac.tuwien.sepm.groupphase.backend.entity.Message;
import at.ac.tuwien.sepm.groupphase.backend.entity.Spot;
import at.ac.tuwien.sepm.groupphase.backend.exception.NotFoundInDatabaseException;
import at.ac.tuwien.sepm.groupphase.backend.exception.ServiceException;
import at.ac.tuwien.sepm.groupphase.backend.exception.ValidationException;
import at.ac.tuwien.sepm.groupphase.backend.repository.CategoryRepository;
import at.ac.tuwien.sepm.groupphase.backend.repository.LocationRepository;
import at.ac.tuwien.sepm.groupphase.backend.repository.SpotRepository;
import at.ac.tuwien.sepm.groupphase.backend.service.LocationService;
import at.ac.tuwien.sepm.groupphase.backend.service.SpotService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
@RequiredArgsConstructor
@Slf4j
public class SimpleSpotService implements SpotService {

    private final SpotRepository spotRepository;
    private final LocationService locationService;
    private final LocationRepository locationRepository;
    private final CategoryRepository categoryRepository;
    private final MessageMapper messageMapper;
    private final ObjectMapper objectMapper;

    /**
     * All emitters (clients who observe so to speak) are stored in a currency save list,
     * that way a client can subscribe to particular spot and receive updates about that spot
     * regarding messages and reactions.
     */
    private final Map<Long, List<SseEmitter>> emitterMap = new ConcurrentHashMap<>();

    @Override
    public Spot create(Spot spot) throws ValidationException, ServiceException {
        log.debug("Create new Spot {}", spot);
        if (spot.getId() != null) {
            throw new ValidationException("Id must be null");
        }
        if (spot.getCategory().getId() == null) {
            throw new ValidationException("Spot must have a Category");
        }
        if (categoryRepository.findById(spot.getCategory().getId()).isEmpty()) {
            throw new ValidationException("Category does not Exist");
        }
        if (spot.getLocation().getId() == null) {
            try {
                spot.setLocation(locationService.create(spot.getLocation()));
            } catch (ValidationException e) {
                throw new ServiceException(e.getMessage());
            }
        }
        if (locationRepository.findById(spot.getLocation().getId()).isEmpty()) {
            throw new ValidationException("Location does not Exist");
        }
        return spotRepository.save(spot);
    }

    //ToDo: Fix notFoundinDatabaseException
    @Override
    public void deleteById(Long id) throws NotFoundInDatabaseException {
        log.debug("Delete Spot with id {}", id);
        try {
            spotRepository.deleteById(id);
        } catch (EmptyResultDataAccessException e) {
            throw new NotFoundInDatabaseException("Spot does not exist");
        }
    }

    @Override
    public SseEmitter subscribe(Long id) {
        SseEmitter emitter = new SseEmitter(Long.MAX_VALUE);

        List<SseEmitter> emitterList;

        if (emitterMap.containsKey(id)) {
            emitterList = emitterMap.get(id);
        } else {
            emitterList = new ArrayList<>();
            emitterMap.put(id, emitterList);
        }

        emitter.onCompletion(() -> log.info("Emitter has completed"));

        try {
            emitter.send(SseEmitter.event().name("INIT"));
            log.info("SENT INIT");
        } catch (IOException | IllegalStateException e) {
            return null;
        }
        emitterList.add(emitter);
        return emitter;
    }

    @Override
    public void dispatch(Message message) {
        Long spotId = message.getSpot().getId();
        if (!emitterMap.containsKey(spotId)) {
            return;
        }

        List<SseEmitter> completedEmitters = new ArrayList<>();
        List<SseEmitter> emitterList = emitterMap.get(spotId);
        emitterList.forEach(emitter -> {
            try {
                log.info("Sending message: {}", message.getContent());
                emitter.send(SseEmitter.event()
                    .name("message")
                    .data(objectMapper.writeValueAsString(messageMapper.messageToMessageDto(message))));
            } catch (IOException | IllegalStateException e) {
                completedEmitters.add(emitter);
                emitter.completeWithError(e);
                log.info("Error while sending event");
            }
        });
        if (!completedEmitters.isEmpty()) {
            log.info("Remove Emitters: {}", completedEmitters);
            emitterList.removeAll(completedEmitters);
        }
    }
}
