package at.ac.tuwien.sepm.groupphase.backend.service.impl;
import at.ac.tuwien.sepm.groupphase.backend.entity.*;
import at.ac.tuwien.sepm.groupphase.backend.exception.*;
import at.ac.tuwien.sepm.groupphase.backend.repository.*;
import at.ac.tuwien.sepm.groupphase.backend.service.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import javax.swing.text.html.Option;
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
    private final UserService userService;


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
        Optional<ApplicationUser> owner = userRepository.findApplicationUserByEmail(SecurityContextHolder.getContext().getAuthentication().getName());
        if (owner.isEmpty()) {
            throw new ValidationException("User not present!");
        } else {
            spot.setOwner(userRepository.findApplicationUserByEmail(SecurityContextHolder.getContext().getAuthentication().getName()).get());
            Spot savedSpot = spotRepository.save(spot);
            hashtagService.acquireHashtags(spot);
            return savedSpot;
        }
    }

    @Override
    public Spot update(Spot spot) throws NotFoundException2, ValidationException, WrongUserException {
        var optionalSpot = spotRepository.findById(spot.getId());
        if (optionalSpot.isEmpty()){
            throw new NotFoundException2("Spot does not Exist");
        }else{
            if(!SecurityContextHolder.getContext().getAuthentication().getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"))){
                if(optionalSpot.get().getOwner()==null){
                    throw new WrongUserException("You can only edit your own spots");
                }else if(!optionalSpot.get().getOwner().getEmail().equals(SecurityContextHolder.getContext().getAuthentication().getName())){
                    throw new WrongUserException("You can only edit your own spots");
                }
            }
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
        }else if(!SecurityContextHolder.getContext().getAuthentication().getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"))){
            if(spot.get().getOwner()==null){
                throw new WrongUserException("You can only edit your own spots");
            }else if(!spot.get().getOwner().getEmail().equals(SecurityContextHolder.getContext().getAuthentication().getName())){
                throw new WrongUserException("You can only edit your own spots");
            }
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

    @Override
    public List<Spot> findSpotsByUserId(Long userId) throws NotFoundException2 {
        Optional<ApplicationUser> owner = this.userRepository.findApplicationUserById(userId);
        if (owner.isPresent()) {
            return this.spotRepository.findByOwner(owner.get());
        } else {
            throw new NotFoundException2("User with ID " + userId + " cannot be found!");
        }
    }
}
