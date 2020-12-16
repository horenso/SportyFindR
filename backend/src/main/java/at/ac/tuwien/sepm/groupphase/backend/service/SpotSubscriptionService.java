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
     * Dispatches a message to all SseEmitters subscribed to the spot of the message
     *
     * @param message, spotId determines to which SseEmitters the message gets sent
     */
    void dispatchNewMessage(Message message);
}
