package at.ac.tuwien.sepm.groupphase.backend.service.impl;

import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.Filter.MessageFilter;
import at.ac.tuwien.sepm.groupphase.backend.entity.ApplicationUser;
import at.ac.tuwien.sepm.groupphase.backend.entity.Message;
import at.ac.tuwien.sepm.groupphase.backend.entity.Reaction;
import at.ac.tuwien.sepm.groupphase.backend.exception.NotFoundException2;
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
    public List<Message> findBySpot(Long spotId) throws NotFoundException2{
        if (spotRepository.findById(spotId).isEmpty()) {
            throw new NotFoundException2(String.format("Spot with id %d not found.", spotId));
        }
        log.debug("Find all messages");

        deleteExpiredMessages();

        List<Message> messageList = messageRepository.findBySpotIdOrderByPublishedAtAsc(spotId);
        messageList.forEach(this::setReactions);
        return messageList;
    }

    @Override
    public Page<Message> findBySpotPaged(Long spotId, Pageable pageable) throws NotFoundException2 {
        if (spotRepository.findById(spotId).isEmpty()) {
            throw new NotFoundException2(String.format("Spot with id %d not found.", spotId));
        }
        log.debug("Find all messages");

        deleteExpiredMessages();

        Page<Message> result = messageRepository.findAllBySpotId(spotId, pageable);
        result.forEach(this::setReactions);
        return result;
    }

    @Override
    public Message create(Message message) throws NotFoundException2, ValidationException {
        log.debug("create message in spot with id {}", message.getSpot().getId());

        MessageValidation.validateNewMessage(message);

        if (spotRepository.findById(message.getSpot().getId()).isEmpty()) {
            throw new NotFoundException2("Spot does not Exist");
        }
        message.setPublishedAt(LocalDateTime.now());
        message.setOwner(userRepository.findApplicationUserByEmail(SecurityContextHolder.getContext().getAuthentication().getName()).get());
        Message savedMessage = messageRepository.save(message);
        hashtagService.acquireHashtags(message);
        spotSubscriptionService.dispatchNewMessage(savedMessage);
        return savedMessage;
    }

    @Override
    public Message getById(Long id) throws NotFoundException2 {
        log.debug("get message with id {}", id);
        deleteExpiredMessages();
        Optional<Message> messageOptional = messageRepository.findById(id);
        if (messageOptional.isEmpty()) {
            throw new NotFoundException2("No messages found");
        }
        Message message = messageOptional.get();
        setReactions(message);
        return message;
    }

    @Override
    public void deleteById(Long id) throws NotFoundException2, WrongUserException {
        Optional<Message> messageOptional = messageRepository.findById(id);
        if (messageOptional.isEmpty()) {
            throw new NotFoundException2(String.format("No message with id %d found!", id));
        }else if (!messageOptional.get().getOwner().getEmail().equals(SecurityContextHolder.getContext().getAuthentication().getName())&&!SecurityContextHolder.getContext().getAuthentication().getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"))){
            throw new WrongUserException("You can only delete your own messages");
        }
        hashtagService.deleteMessageInHashtags(messageOptional.get());
        reactionRepository.deleteAllByMessage_Id(id);
        messageRepository.deleteById(id);
        spotSubscriptionService.dispatchDeletedMessage(messageOptional.get().getSpot().getId(), id);
    }
    @Override
    public void deleteByIdWithoutAuthentication(Long id) throws NotFoundException2 {
        Optional<Message> messageOptional = messageRepository.findById(id);
        if (messageOptional.isEmpty()) {
            throw new NotFoundException2(String.format("No message with id %d found!", id));
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
        Reaction reaction= reactionRepository.getReactionByOwnerEmail(SecurityContextHolder.getContext().getAuthentication().getName(),message.getId());
        if(reaction==null){
            message.setOwnerReaction(null);
            message.setOwnerReactionId(null);
        } else if(reaction.getType().equals(Reaction.ReactionType.THUMBS_DOWN)){
            message.setOwnerReaction(Reaction.ReactionType.THUMBS_DOWN);
            message.setOwnerReactionId(reaction.getId());
        }else{
            message.setOwnerReaction(Reaction.ReactionType.THUMBS_UP);
            message.setOwnerReactionId(reaction.getId());
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

        if (messageFilter.getHashtagName() != null && !messageFilter.getHashtagName().equals("")) {
            return messageRepository.filterHash(messageFilter.getCategoryId(), messageFilter.getUser(), messageFilter.getTime(), messageFilter.getHashtagName(), pageable);
        }
        return messageRepository.filter(messageFilter.getCategoryId(), messageFilter.getUser(), messageFilter.getTime(), pageable);
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
    public List<Message> findByOwner(Long userId) throws NotFoundException2 {
        Optional<ApplicationUser> owner = this.userRepository.findById(userId);
        if (owner.isPresent()) {
            return this.messageRepository.findByOwner(owner.get());
        } else {
            throw new NotFoundException2("User with ID " + userId + " not found.");
        }
    }
}
