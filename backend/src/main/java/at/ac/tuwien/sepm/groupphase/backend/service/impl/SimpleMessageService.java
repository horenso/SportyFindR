package at.ac.tuwien.sepm.groupphase.backend.service.impl;

import at.ac.tuwien.sepm.groupphase.backend.entity.Location;
import at.ac.tuwien.sepm.groupphase.backend.entity.Message;
import at.ac.tuwien.sepm.groupphase.backend.entity.Spot;
import at.ac.tuwien.sepm.groupphase.backend.exception.NotFoundException;
import at.ac.tuwien.sepm.groupphase.backend.exception.ServiceException;
import at.ac.tuwien.sepm.groupphase.backend.exception.ValidationException;
import at.ac.tuwien.sepm.groupphase.backend.repository.MessageRepository;
import at.ac.tuwien.sepm.groupphase.backend.service.MessageService;
import at.ac.tuwien.sepm.groupphase.backend.validator.LocationValidator;
import at.ac.tuwien.sepm.groupphase.backend.validator.MessageValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.lang.invoke.MethodHandles;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class SimpleMessageService implements MessageService {

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    private final MessageRepository messageRepository;
    private final MessageValidator validator;

    public SimpleMessageService(MessageRepository messageRepository, MessageValidator validator) {
        this.messageRepository = messageRepository;
        this.validator = validator;
    }

    @Override
    public List<Message> findBySpot(Long spotId) {
        LOGGER.debug("Find all messages");
        return messageRepository.findBySpotIdOrderByPublishedAtDesc(spotId);
    }

    @Override
    public Message create(Message message) {
        LOGGER.debug("create message in spot with id {}", message.getSpot().getId());
        message.setPublishedAt(LocalDateTime.now());
        return messageRepository.save(message);
    }

    @Override
    public List<Message> filter(Long categoryId, Double latitude, Double longitude, Double radius, LocalDateTime time) throws NotFoundException, ServiceException {
        LOGGER.debug("Searching for messages of spots within a distance of at most " + radius + " km, belonging to the category " + categoryId + ",not older than: " + time);

        if (categoryId == null){
            categoryId = 0L;
        }

        if (latitude == null){
            latitude = 0.0;
        }

        if (longitude == null){
            longitude = 0.0;
        }

        if (radius == null){
            radius = 0.0;
        }

        if (time == null){
            time = LocalDateTime.MIN;
        }

        List<Message> messages = messageRepository.filter(categoryId, time);

        if (messages.isEmpty()) {
            LOGGER.error("No Messages with these parameters found.");
            throw new ServiceException("No Messages with these parameters found.");
        } else {
            try {
                if (radius != 0) {      // if search parameters contain radius data
                    LOGGER.debug("radius > 0");
                    return validator.validateLocationDistance(latitude, longitude, radius, messages);
                } else {
                    LOGGER.debug("no radius given: search by category & time only");
                    return messages;       // search by category and time only
                }
            } catch (ValidationException e) {
                LOGGER.error("Invalid Data.");
                throw new ServiceException(e.getMessage());
            }
        }

    }
}
