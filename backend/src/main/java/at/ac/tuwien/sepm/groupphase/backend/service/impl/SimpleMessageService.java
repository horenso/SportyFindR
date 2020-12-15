package at.ac.tuwien.sepm.groupphase.backend.service.impl;

import at.ac.tuwien.sepm.groupphase.backend.entity.Message;
import at.ac.tuwien.sepm.groupphase.backend.exception.NotFoundException;
import at.ac.tuwien.sepm.groupphase.backend.exception.ServiceException;
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
        return messageRepository.findBySpotIdOrderByPublishedAtAsc(spotId);
    }

    @Override
    public Message create(Message message) {
        log.debug("create message in spot with id {}", message.getSpot().getId());
        message.setPublishedAt(LocalDateTime.now());
        Message savedMessage = messageRepository.save(message);
        spotService.dispatch(savedMessage);
        return savedMessage;
    }

    @Override
    public Message getById(Long id) throws ServiceException {
        log.debug("get message with id {}", id);
        try {
            return messageRepository.findById(id).get();
        } catch (NotFoundException e){
            throw new ServiceException (e.getMessage());
        }
    }
}
