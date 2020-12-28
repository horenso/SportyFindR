package at.ac.tuwien.sepm.groupphase.backend.service.impl;

import at.ac.tuwien.sepm.groupphase.backend.entity.Hashtag;
import at.ac.tuwien.sepm.groupphase.backend.entity.Message;
import at.ac.tuwien.sepm.groupphase.backend.entity.Reaction;
import at.ac.tuwien.sepm.groupphase.backend.exception.NotFoundException;
import at.ac.tuwien.sepm.groupphase.backend.exception.ServiceException;
import at.ac.tuwien.sepm.groupphase.backend.exception.ValidationException;
import at.ac.tuwien.sepm.groupphase.backend.repository.HashtagRepository;
import at.ac.tuwien.sepm.groupphase.backend.repository.MessageRepository;
import at.ac.tuwien.sepm.groupphase.backend.repository.ReactionRepository;
import at.ac.tuwien.sepm.groupphase.backend.repository.SpotRepository;
import at.ac.tuwien.sepm.groupphase.backend.service.HashtagService;
import at.ac.tuwien.sepm.groupphase.backend.service.MessageService;
import at.ac.tuwien.sepm.groupphase.backend.service.SpotService;
import at.ac.tuwien.sepm.groupphase.backend.service.SpotSubscriptionService;
import at.ac.tuwien.sepm.groupphase.backend.validator.MessageValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
@Slf4j
public class SimpleMessageService implements MessageService {

    private final MessageRepository messageRepository;
    private final ReactionRepository reactionRepository;
    private final SpotRepository spotRepository;
    private final HashtagService hashtagService;
    private final SpotSubscriptionService spotSubscriptionService;
    private final MessageValidator validator;

    @Override
    public List<Message> findBySpot(Long spotId) {
        if (spotRepository.getOne(spotId) == null) {
            throw new NotFoundException(String.format("Spot with id %d not found.", spotId));
        }
        log.debug("Find all messages");
        List<Message> messageList = messageRepository.findBySpotIdOrderByPublishedAtAsc(spotId);
        // TODO: THIS IS VERY INEFFICIENT!
        messageList.forEach(this::setReactions);
        return messageList;
    }

    @Override
    public Message create(Message message) {
        log.debug("create message in spot with id {}", message.getSpot().getId());
        message.setPublishedAt(LocalDateTime.now());
        Message savedMessage = messageRepository.save(message);
        hashtagService.getHashtags(message);
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
        hashtagService.deleteMessageInHashtags(messageOptional.get());
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

    @Override
    public List<Message> filter(Long categoryId,
                                Double latitude,
                                Double longitude,
                                Double radius,
                                LocalDateTime time) throws NotFoundException, ServiceException {
        log.debug("Searching for messages of spots within a distance of at most " + radius + " km, belonging to the category " + categoryId + ",not older than: " + time);

        if (categoryId == null) {
            categoryId = 0L;
        }

        if (latitude == null) {
            latitude = 0.0;
        }

        if (longitude == null) {
            longitude = 0.0;
        }

        if (radius == null) {
            radius = 0.0;
        }

        if (time == null) {
            time = LocalDateTime.MIN;
        }

        List<Message> messages = messageRepository.filter(categoryId, time);

        if (messages.isEmpty()) {
            log.error("No Messages with these parameters found.");
            throw new ServiceException("No Messages with these parameters found.");
        } else {
            try {
                if (radius != 0) {      // if search parameters contain radius data
                    log.debug("radius > 0");
                    return validator.validateLocationDistance(latitude, longitude, radius, messages);
                } else {
                    log.debug("no radius given: search by category & time only");
                    return messages;       // search by category and time only
                }
            } catch (ValidationException e) {
                log.error("Invalid Data.");
                throw new ServiceException(e.getMessage());
            }
        }

    }


}
