package at.ac.tuwien.sepm.groupphase.backend.service.impl;

import at.ac.tuwien.sepm.groupphase.backend.endpoint.mapper.MessageMapper;
import at.ac.tuwien.sepm.groupphase.backend.entity.Location;
import at.ac.tuwien.sepm.groupphase.backend.entity.Message;
import at.ac.tuwien.sepm.groupphase.backend.entity.Spot;
import at.ac.tuwien.sepm.groupphase.backend.exception.NotFoundException;
import at.ac.tuwien.sepm.groupphase.backend.exception.ServiceException;
import at.ac.tuwien.sepm.groupphase.backend.exception.ValidationException;
import at.ac.tuwien.sepm.groupphase.backend.repository.*;
import at.ac.tuwien.sepm.groupphase.backend.service.LocationService;
import at.ac.tuwien.sepm.groupphase.backend.service.SpotService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.List;
import java.util.Map;
import java.util.Optional;
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
    private final MessageRepository messageRepository;
    private final ReactionRepository reactionRepository;

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

    @Override
    public Spot update(Spot spot) throws ServiceException {
        return null; // TODO: merge
    }

    @Override
    public void deleteById(Long id) throws ValidationException {
        log.debug("Delete Spot with id {}", id);
        var spot = spotRepository.findById(id);
        if (spot.isEmpty()) {
            throw new ValidationException("Spot does not exist");
        }

        List<Message> messages = messageRepository.findAllBySpot_Id(id);
        for (Message message : messages) {
            reactionRepository.deleteAllByMessage_Id(message.getId());
            messageRepository.deleteById(message.getId());
        }

        spotRepository.deleteById(id);
        if (spotRepository.findLocationWithSpot(spot.get().getLocation().getId()).isEmpty()) {
            locationRepository.deleteById(spot.get().getLocation().getId());
        }
    }

    @Override
    public List<Spot> getSpotsByLocation(Long locationId) throws ValidationException {
        //Message message;
        Optional<Location> optionalLocation = locationRepository.findById(locationId);
        if (optionalLocation.isEmpty()) {
            throw new ValidationException("Location with ID " + locationId + " cannot be found!");
        } else {
            return spotRepository.getSpotsByLocationId(locationId);
        }
    }

    @Override
    public Spot getOneById(Long spotId) {
        Optional<Spot> spotOptional = this.spotRepository.getOneById(spotId);
        if (spotOptional.isEmpty()) {
            throw new NotFoundException("Spot with ID " + spotId + " cannot be found!");
        }
        return spotOptional.get();
    }
}
