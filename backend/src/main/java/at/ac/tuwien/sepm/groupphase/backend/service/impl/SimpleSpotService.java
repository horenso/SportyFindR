package at.ac.tuwien.sepm.groupphase.backend.service.impl;
import at.ac.tuwien.sepm.groupphase.backend.entity.Location;
import at.ac.tuwien.sepm.groupphase.backend.entity.Message;
import at.ac.tuwien.sepm.groupphase.backend.entity.Spot;
import at.ac.tuwien.sepm.groupphase.backend.exception.*;
import at.ac.tuwien.sepm.groupphase.backend.repository.*;
import at.ac.tuwien.sepm.groupphase.backend.service.HashtagService;
import at.ac.tuwien.sepm.groupphase.backend.service.LocationService;
import at.ac.tuwien.sepm.groupphase.backend.service.MessageService;
import at.ac.tuwien.sepm.groupphase.backend.service.SpotService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
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
    private final MessageService messageService;
    private final HashtagService hashtagService;
    private final LocationRepository locationRepository;
    private final CategoryRepository categoryRepository;
    private final UserRepository userRepository;


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
        spot.setOwner(userRepository.findApplicationUserByEmail(SecurityContextHolder.getContext().getAuthentication().getName()).get());
        Spot savedSpot = spotRepository.save(spot);
        hashtagService.acquireHashtags(spot);
        return savedSpot;
    }

    @Override
    public Spot update(Spot spot) throws NotFoundException2, ValidationException, WrongUserException {
        var optionalSpot = spotRepository.findById(spot.getId());
        if (optionalSpot.isEmpty()){
            throw new NotFoundException2("Spot does not Exist");
        }else if (!optionalSpot.get().getOwner().getEmail().equals(SecurityContextHolder.getContext().getAuthentication().getName())){
            throw new WrongUserException("You can only edit your own spots");
        }else{
            spot.setOwner(optionalSpot.get().getOwner());
        }
        if (spot.getCategory().getId() == null) {
            throw new ValidationException("Spot must have a Category");
        }
        if (categoryRepository.findById(spot.getCategory().getId()).isEmpty()) {
            throw new ValidationException("Category does not Exist");
        }
        if (locationRepository.findById(spot.getLocation().getId()).isEmpty()) {
            throw new ValidationException("Location does not Exist");
        }
        hashtagService.deleteSpotInHashtags(spot);
        hashtagService.acquireHashtags(spot);
        return spotRepository.save(spot);
    }
    @Override
    public boolean deleteById(Long id) throws ValidationException, ServiceException, WrongUserException {
        log.debug("Delete Spot with id {}", id);
        var spot = spotRepository.findById(id);
        if (spot.isEmpty()) {
            throw new ValidationException("Spot does not exist");
        }else if (!spot.get().getOwner().getEmail().equals(SecurityContextHolder.getContext().getAuthentication().getName())){
            throw new WrongUserException("You can only delete your own spots");
        }
        try {
            List<Message> messages = messageService.findBySpot(id);
            for (Message message : messages) {
                messageService.deleteByIdWithoutAuthentication(message.getId());
            }
        }catch (NotFoundException2 e){
            throw new ServiceException(e.getMessage());
        }
        hashtagService.deleteSpotInHashtags(spotRepository.findById(id).get());
        spotRepository.deleteById(id);
        if (spotRepository.findLocationWithSpot(spot.get().getLocation().getId()).isEmpty()) {
            locationRepository.deleteById(spot.get().getLocation().getId());
            return true;
        } else {
            return false;
        }
    }

    @Override
    public List<Spot> getSpotsByLocation(Long locationId) throws ValidationException {
        //Message message;
        Optional<Location> optionalLocation = locationRepository.getOneById(locationId);
        if (optionalLocation.isEmpty()) {
            throw new ValidationException("Location with ID " + locationId + " cannot be found!");
        } else {
            return spotRepository.getSpotsByLocationId(locationId);
        }
    }

    @Override
    public Spot getOneById(Long spotId) throws NotFoundException2{
        Optional<Spot> spotOptional = this.spotRepository.getOneById(spotId);
        if (spotOptional.isEmpty()) {
            throw new NotFoundException2("Spot with ID " + spotId + " cannot be found!");
        }
        return spotOptional.get();
    }
}
