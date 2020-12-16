package at.ac.tuwien.sepm.groupphase.backend.service.impl;

import at.ac.tuwien.sepm.groupphase.backend.endpoint.mapper.MessageMapper;
import at.ac.tuwien.sepm.groupphase.backend.entity.Message;
import at.ac.tuwien.sepm.groupphase.backend.service.SpotSubscriptionService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
public class SpotSubscriptionServiceImpl implements SpotSubscriptionService {

    private final MessageMapper messageMapper;
    private final ObjectMapper objectMapper;

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
            log.debug("SENT INIT");
        } catch (IOException | IllegalStateException e) {
            return null;
        }
        emitterList.add(emitter);
        return emitter;
    }

    @Override
    public void dispatchNewMessage(Message message) {
        Long spotId = message.getSpot().getId();
        if (!emitterMap.containsKey(spotId)) {
            return;
        }

        List<SseEmitter> completedEmitters = new ArrayList<>();
        List<SseEmitter> emitterList = emitterMap.get(spotId);
        emitterList.forEach(emitter -> {
            try {
                log.debug("Sending message: {}", message.getContent());
                emitter.send(SseEmitter.event()
                    .name("message")
                    .data(objectMapper.writeValueAsString(messageMapper.messageToMessageDto(message))));
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
