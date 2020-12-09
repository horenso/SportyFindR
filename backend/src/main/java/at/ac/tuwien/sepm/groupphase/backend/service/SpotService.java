package at.ac.tuwien.sepm.groupphase.backend.service;

import at.ac.tuwien.sepm.groupphase.backend.entity.Message;
import at.ac.tuwien.sepm.groupphase.backend.entity.Spot;
import at.ac.tuwien.sepm.groupphase.backend.exception.NotFoundInDatabaseException;
import at.ac.tuwien.sepm.groupphase.backend.exception.ServiceException;
import at.ac.tuwien.sepm.groupphase.backend.exception.ValidationException;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

public interface SpotService {

    /**
     * Create a Spot entry
     *
     * @param spot to create
     * @return created spot entry
     */
    Spot create(Spot spot) throws ServiceException, ValidationException;

    void deleteById(Long id) throws NotFoundInDatabaseException;

    SseEmitter subscribe(Long id);

    /**
     * Dispatches a message to all SseEmitters subscribed to the spot of the message
     *
     * @param message, spotId determines to which SseEmitters the message gets sent
     */
    void dispatch(Message message);
}
