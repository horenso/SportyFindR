package at.ac.tuwien.sepm.groupphase.backend.service;

import at.ac.tuwien.sepm.groupphase.backend.entity.Message;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

public interface SpotSubscriptionService {

    /**
     * Allows to subscribe to a specific spot, events get fired each time
     * a new message or reaction happens in the spot
     *
     * @param id of the spot to subscribe to
     * @return SseEmitter that is supplied with updates until sending an event fails
     */
    SseEmitter subscribe(Long id);

    /**
     * Dispatches a newly created message to all subscribers
     * Event name: 'message/new'
     *
     * @param message, spotId determines to which SseEmitters the message gets sent
     */
    void dispatchNewMessage(Message message);

    /**
     * Dispatches an event that a message was deleted to all subscribers
     * Event name: 'message/delete'
     *
     * @param spotId    of the spot that contained the deleted message
     * @param messageId of the message that was just deleted
     */
    void dispatchDeletedMessage(Long spotId, Long messageId);

    /**
     * Dispatches a new reaction (from one user to one message) to all subscribers
     * Event name: 'message/updateReaction'
     *
     * @param messageId of the message that has changed
     */
    void dispatchMessageWithUpdatedReactions(Long messageId);
}
