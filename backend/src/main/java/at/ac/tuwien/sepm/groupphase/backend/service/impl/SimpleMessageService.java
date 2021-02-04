package at.ac.tuwien.sepm.groupphase.backend.service.impl;

import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.Filter.MessageFilter;
import at.ac.tuwien.sepm.groupphase.backend.entity.ApplicationUser;
import at.ac.tuwien.sepm.groupphase.backend.entity.Message;
import at.ac.tuwien.sepm.groupphase.backend.entity.Reaction;
import at.ac.tuwien.sepm.groupphase.backend.exception.NotFoundException;
import at.ac.tuwien.sepm.groupphase.backend.exception.ServiceException;
import at.ac.tuwien.sepm.groupphase.backend.exception.ValidationException;
import at.ac.tuwien.sepm.groupphase.backend.exception.WrongUserException;
import at.ac.tuwien.sepm.groupphase.backend.repository.MessageRepository;
import at.ac.tuwien.sepm.groupphase.backend.repository.ReactionRepository;
import at.ac.tuwien.sepm.groupphase.backend.repository.SpotRepository;
import at.ac.tuwien.sepm.groupphase.backend.repository.UserRepository;
import at.ac.tuwien.sepm.groupphase.backend.service.HashtagService;
import at.ac.tuwien.sepm.groupphase.backend.service.MessageService;
import at.ac.tuwien.sepm.groupphase.backend.service.SpotSubscriptionService;
import at.ac.tuwien.sepm.groupphase.backend.service.validator.MessageValidation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
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
    private final HashtagService hashtagService;
    private final SpotSubscriptionService spotSubscriptionService;
    private final UserRepository userRepository;

    @Override
    public List<Message> findBySpot(Long spotId) throws NotFoundException {
        if (spotRepository.findById(spotId).isEmpty()) {
            throw new NotFoundException(String.format("Spot with id %d not found.", spotId));
        }
        log.debug("Find all messages");

        deleteExpiredMessages();

        List<Message> messageList = messageRepository.findBySpotIdOrderByPublishedAtAsc(spotId);
        messageList.forEach(this::setReactions);
        return messageList;
    }

    @Override
    public Page<Message> findBySpotPaged(Long spotId, Pageable pageable) throws NotFoundException {
        if (spotRepository.findById(spotId).isEmpty()) {
            throw new NotFoundException(String.format("Spot with id %d not found.", spotId));
        }
        log.debug("Find all messages");

        deleteExpiredMessages();

        Page<Message> result = messageRepository.findAllBySpotId(spotId, pageable);
        result.forEach(this::setReactions);
        return result;
    }

    @Override
    public Message create(Message message) throws NotFoundException, ValidationException {
        log.debug("create message in spot with id {}", message.getSpot().getId());

        MessageValidation.validateNewMessage(message);

        if (spotRepository.findById(message.getSpot().getId()).isEmpty()) {
            throw new NotFoundException("Spot does not Exist");
        }
        message.setPublishedAt(LocalDateTime.now());
        message.setOwner(userRepository.findApplicationUserByEmail(SecurityContextHolder.getContext().getAuthentication().getName()).get());
        Message savedMessage = messageRepository.save(message);
        hashtagService.acquireHashtags(message);
        spotSubscriptionService.dispatchNewMessage(savedMessage);
        return savedMessage;
    }

    @Override
    public Message getById(Long id) throws NotFoundException {
        log.debug("get message with id {}", id);
        deleteExpiredMessages();
        Optional<Message> messageOptional = messageRepository.findById(id);
        if (messageOptional.isEmpty()) {
            throw new NotFoundException("No messages found");
        }
        Message message = messageOptional.get();
        setReactions(message);
        return message;
    }

    @Override
    public void deleteById(Long id) throws NotFoundException, WrongUserException, ServiceException {
        Optional<Message> messageOptional = messageRepository.findById(id);
        if (messageOptional.isEmpty()) {
            throw new NotFoundException(String.format("No message with id %d found!", id));
        } else if (!messageOptional.get().getOwner().getEmail().equals(SecurityContextHolder.getContext().getAuthentication().getName()) && !SecurityContextHolder.getContext().getAuthentication().getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"))) {
            throw new WrongUserException("You can only delete your own messages");
        }
        try {
            hashtagService.deleteMessageInHashtags(messageOptional.get());
        } catch (ValidationException e) {
            throw new ServiceException(e);
        }
        reactionRepository.deleteAllByMessage_Id(id);
        messageRepository.deleteById(id);
        spotSubscriptionService.dispatchDeletedMessage(messageOptional.get().getSpot().getId(), id);
    }

    @Override
    public void deleteByIdWithoutAuthentication(Long id) throws NotFoundException, ServiceException {
        Optional<Message> messageOptional = messageRepository.findById(id);
        if (messageOptional.isEmpty()) {
            throw new NotFoundException(String.format("No message with id %d found!", id));
        }
        try {
            hashtagService.deleteMessageInHashtags(messageOptional.get());
        } catch (ValidationException e) {
            throw new ServiceException(e);
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

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth!=null) {
            List<Reaction> reaction = reactionRepository.getReactionByOwnerEmail(SecurityContextHolder.getContext().getAuthentication().getName(), message.getId());
            if (reaction == null || reaction.size() == 0) {
                message.setOwnerReaction(null);
                message.setOwnerReactionId(null);
            } else if (reaction.get(0).getType().equals(Reaction.ReactionType.THUMBS_DOWN)) {
                message.setOwnerReaction(Reaction.ReactionType.THUMBS_DOWN);
                message.setOwnerReactionId(reaction.get(0).getId());
            } else {
                message.setOwnerReaction(Reaction.ReactionType.THUMBS_UP);
                message.setOwnerReactionId(reaction.get(0).getId());
            }
        }
    }

    @Override
    public Page<Message> filter(MessageFilter messageFilter, Pageable pageable) {
        log.debug("Searching for messages of spots belonging to the category " + messageFilter.getCategoryId() + ", not older than: " + messageFilter.getTime());

        if (messageFilter.getCategoryId() == null) {
            messageFilter.setCategoryId(0L);
        }

        if (messageFilter.getTime() == null) {
            messageFilter.setTime(LocalDateTime.MIN);
        }

        if (messageFilter.getHashtagName() == null || messageFilter.getHashtagName().isEmpty()) {
            return messageRepository.filter(messageFilter.getCategoryId(), messageFilter.getUser(), messageFilter.getTime(), pageable);
        }
        return messageRepository.filterHash(messageFilter.getCategoryId(), messageFilter.getUser(), messageFilter.getTime(), messageFilter.getHashtagName(), pageable);
    }


    private void deleteExpiredMessages() {
        List<Message> deletedExpiredMessages = messageRepository.deleteAllByExpirationDateBefore(LocalDateTime.now());
        if (deletedExpiredMessages.size() > 0) {
            deletedExpiredMessages.forEach(message -> {
                log.info("Deleted expired message with Id {}", message.getId());
            });
        }
    }

    @Override
    public List<Message> findByOwner(Long userId) throws NotFoundException {
        Optional<ApplicationUser> owner = this.userRepository.findById(userId);
        if (owner.isPresent()) {
            return this.messageRepository.findByOwner(owner.get());
        } else {
            throw new NotFoundException("User with ID " + userId + " not found.");
        }
    }
}
