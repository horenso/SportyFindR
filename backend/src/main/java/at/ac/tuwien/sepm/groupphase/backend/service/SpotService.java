package at.ac.tuwien.sepm.groupphase.backend.service;

import at.ac.tuwien.sepm.groupphase.backend.entity.Message;
import at.ac.tuwien.sepm.groupphase.backend.entity.Reaction;
import at.ac.tuwien.sepm.groupphase.backend.entity.Spot;
import at.ac.tuwien.sepm.groupphase.backend.exception.ServiceException;
import at.ac.tuwien.sepm.groupphase.backend.exception.ValidationException;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.List;

public interface SpotService {

    /**
     * Create a Spot entry
     *
     * @param spot to create
     * @return created spot entry
     */
    Spot create(Spot spot) throws ServiceException, ValidationException;

    /**
     * Update a Spot entry
     *
     * @param spot to be updated, id determines what spot gets updated
     * @return spot as it is saved in the system
     * @throws ServiceException if the spot entity is not valid
     */
    Spot update(Spot spot) throws ServiceException;

    /**
     * Allows to subscribe to a specific spot, events get fired each time
     * a new message or reaction happens in the spot
     *
     * @param id of the spot to subscribe to
     * @return SseEmitter that is supplied with updates until sending an event fails
     */
    SseEmitter subscribe(Long id);

    /**
     * Dispatches a message to all SseEmitters subscribed to the spot of the message
     *
     * @param message, spotId determines to which SseEmitters the message gets sent
     */
    void dispatch(Message message);

    /**
     * Deletes one spot by id, all messages and reactions associated with that spot get
     * deleted as well.
     *
     * @param id of the spot that should be deleted
     * @throws ValidationException if no spot with this id is known
     */
    /**
     * Delete a Spot entry
     *
     * @param id of spot to delete
     */
    void deleteById(Long id) throws ValidationException;

    List<Spot> getSpotsByLocation(Long locationId);
}
