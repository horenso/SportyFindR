package at.ac.tuwien.sepm.groupphase.backend.service.impl;

import at.ac.tuwien.sepm.groupphase.backend.endpoint.mapper.MessageMapper;
import at.ac.tuwien.sepm.groupphase.backend.entity.Message;
import at.ac.tuwien.sepm.groupphase.backend.entity.Reaction;
import at.ac.tuwien.sepm.groupphase.backend.repository.MessageRepository;
import at.ac.tuwien.sepm.groupphase.backend.repository.ReactionRepository;
import at.ac.tuwien.sepm.groupphase.backend.service.SpotSubscriptionService;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Service
@Slf4j
public class SseSubscriptionService implements SpotSubscriptionService {

    private final MessageMapper messageMapper;
    private final ReactionRepository reactionRepository;
    private final MessageRepository messageRepository;
    private final ObjectMapper objectMapper;

    public SseSubscriptionService(MessageMapper messageMapper,
                                  ReactionRepository reactionRepository,
                                  MessageRepository messageRepository,
                                  ObjectMapper objectMapper) {
        this.messageMapper = messageMapper;
        this.reactionRepository = reactionRepository;
        this.messageRepository = messageRepository;
        this.objectMapper = objectMapper;

        objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
    }

    /**
     * All emitters (clients who observe so to speak) are stored in a currency save list,
     * that way a client can subscribe to particular spot and receive updates about that spot
     * regarding messages and reactions.
     */
    private final Map<Long, List<SseEmitter>> emitterMap = new ConcurrentHashMap<>();

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
            log.debug("sent INIT");
        } catch (IOException | IllegalStateException e) {
            return null;
        }
        emitterList.add(emitter);
        return emitter;
    }

    @Override
    public void dispatchNewMessage(Message message) {
        dispatch(message.getSpot().getId(), messageMapper.messageToMessageDto(message), "message/new");
    }

    @Override
    public void dispatchDeletedMessage(Long spotId, Long messageId) {
        dispatch(spotId, Message.builder().id(messageId).build(), "message/delete");
    }

    @Override
    public void dispatchMessageWithUpdatedReactions(Long messageId) {
        Optional<Message> messageOptional = messageRepository.findById(messageId);
        if (messageOptional.isEmpty()) {
            return;
        }
        Message message = messageOptional.get();
        message.setUpVotes(
            reactionRepository.countReactionByMessage_IdAndType(message.getId(), Reaction.ReactionType.THUMBS_UP));
        message.setDownVotes(
            reactionRepository.countReactionByMessage_IdAndType(message.getId(), Reaction.ReactionType.THUMBS_DOWN));
        // This information is excluded in the event
        message.setPublishedAt(null);
        message.setContent(null);
        Long spotId = message.getSpot().getId();
        message.setSpot(null);

        dispatch(spotId, message, "message/updateReaction");
    }

    private void dispatch(Long spotId, Object object, String destination) {
        if (!emitterMap.containsKey(spotId)) {
            return;
        }

        List<SseEmitter> completedEmitters = new ArrayList<>();
        List<SseEmitter> emitterList = emitterMap.get(spotId);
        emitterList.forEach(emitter -> {
            try {
                String event = objectMapper.writeValueAsString(object);

                log.debug("Sending: {}", event);
                emitter.send(SseEmitter.event()
                    .name(destination)
                    .data(event));
            } catch (IOException | IllegalStateException e) {
                completedEmitters.add(emitter);
                emitter.completeWithError(e);
            }
        });

        if (!completedEmitters.isEmpty()) {
            log.debug("Remove Emitters: {}", completedEmitters);
            emitterList.removeAll(completedEmitters);
        }
    }
}
