package at.ac.tuwien.sepm.groupphase.backend.service.impl;

import at.ac.tuwien.sepm.groupphase.backend.entity.Message;
import at.ac.tuwien.sepm.groupphase.backend.repository.MessageRepository;
import at.ac.tuwien.sepm.groupphase.backend.service.MessageService;
import at.ac.tuwien.sepm.groupphase.backend.service.SpotService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class SimpleMessageService implements MessageService {

    private final MessageRepository messageRepository;
    private final SpotService spotService;

    @Override
    public List<Message> findBySpot(Long spotId) {
        log.debug("Find all messages");
        return messageRepository.findBySpotIdOrderByPublishedAtDesc(spotId);
    }

    @Override
    public Message create(Message message) {
        log.debug("create message in spot with id {}", message.getSpot().getId());
        message.setPublishedAt(LocalDateTime.now());
        spotService.dispatch(message);
        return messageRepository.save(message);
    }
}
