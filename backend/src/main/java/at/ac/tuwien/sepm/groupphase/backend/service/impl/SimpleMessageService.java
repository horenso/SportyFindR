package at.ac.tuwien.sepm.groupphase.backend.service.impl;

import at.ac.tuwien.sepm.groupphase.backend.entity.Message;
import at.ac.tuwien.sepm.groupphase.backend.entity.Reaction;
import at.ac.tuwien.sepm.groupphase.backend.exception.NotFoundException;
import at.ac.tuwien.sepm.groupphase.backend.exception.ServiceException;
import at.ac.tuwien.sepm.groupphase.backend.repository.MessageRepository;
import at.ac.tuwien.sepm.groupphase.backend.repository.ReactionRepository;
import at.ac.tuwien.sepm.groupphase.backend.repository.SpotRepository;
import at.ac.tuwien.sepm.groupphase.backend.service.MessageService;
import at.ac.tuwien.sepm.groupphase.backend.service.SpotSubscriptionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class SimpleMessageService implements MessageService {

    private final MessageRepository messageRepository;
    private final ReactionRepository reactionRepository;
    private final SpotRepository spotRepository;
    private final SpotSubscriptionService spotSubscriptionService;

    @Override
    public List<Message> findBySpot(Long spotId) {
        if (spotRepository.getOne(spotId) == null) {
            throw new NotFoundException(String.format("Spot with id %d not found.", spotId));
        }
        log.debug("Find all messages");
        List<Message> messageList = messageRepository.findBySpotIdOrderByPublishedAtAsc(spotId);
        // TODO: THIS IS VERY INEFFICIENT!
        messageList.forEach(message -> {
            setReactions(message);
        });
        return messageList;
    }

    @Override
    public Message create(Message message) {
        log.debug("create message in spot with id {}", message.getSpot().getId());
        message.setPublishedAt(LocalDateTime.now());
        Message savedMessage = messageRepository.save(message);
        spotSubscriptionService.dispatchNewMessage(savedMessage);
        return savedMessage;
    }

    @Override
    public Message getById(Long id) throws ServiceException {
        log.debug("get message with id {}", id);
        Optional<Message> messageOptional = messageRepository.findById(id);
        if (messageOptional.isEmpty()) {
            throw new NotFoundException();
        }
        Message message = messageOptional.get();
        setReactions(message);
        return message;
    }

    @Override
    public void deleteById(Long id) throws NotFoundException {
        Optional<Message> messageOptional = messageRepository.findById(id);
        if (messageOptional.isEmpty()) {
            throw new NotFoundException(String.format("No message with id %d found!", id));
        }
        reactionRepository.deleteAllByMessage_Id(id);
        messageRepository.deleteById(id);
        spotSubscriptionService.dispatchDeletedMessage(messageOptional.get().getSpot().getId(), id);
    }

    private void setReactions(Message message) {
        message.setUpVotes(
            reactionRepository.countReactionByMessage_IdAndType(message.getId(), Reaction.ReactionType.THUMBS_UP));
        message.setDownVotes(
            reactionRepository.countReactionByMessage_IdAndType(message.getId(), Reaction.ReactionType.THUMBS_DOWN));
    }
}
