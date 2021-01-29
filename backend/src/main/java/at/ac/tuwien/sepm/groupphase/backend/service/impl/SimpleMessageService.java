package at.ac.tuwien.sepm.groupphase.backend.service.impl;

import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.MessageSearchObject;
import at.ac.tuwien.sepm.groupphase.backend.entity.Hashtag;
import at.ac.tuwien.sepm.groupphase.backend.entity.Message;
import at.ac.tuwien.sepm.groupphase.backend.entity.Reaction;
import at.ac.tuwien.sepm.groupphase.backend.exception.NotFoundException2;
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
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.LinkedList;
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
        List<Message> messageList = messageRepository.findBySpotIdOrderByPublishedAtAsc(spotId);
        // TODO: THIS IS VERY INEFFICIENT!
        messageList.forEach(this::setReactions);
        return messageList;
    }

    @Override
    public Page<Message> findBySpotPaged(Long spotId, Pageable pageable) throws NotFoundException2{
        if (spotRepository.findById(spotId).isEmpty()) {
            throw new NotFoundException2(String.format("Spot with id %d not found.", spotId));
        }
        log.debug("Find all messages");

        List<Message> messageList = messageRepository.findBySpotIdOrderByPublishedAtAsc(spotId);
        // TODO: THIS IS VERY INEFFICIENT!
        messageList.forEach(this::setReactions);

        List<Long> messageIdList = messageRepository.findBySpotIdOrderByPublishedAtAscLong(spotId);

        return messageRepository.findByIdIn(messageIdList, pageable);
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
        hashtagService.getHashtags(message);
        spotSubscriptionService.dispatchNewMessage(savedMessage);
        return savedMessage;
    }

    @Override
    public Message getById(Long id) throws NotFoundException2 {
        log.debug("get message with id {}", id);
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
    }

    @Override
    public Page<Message> filter(MessageSearchObject messageSearchObject, Pageable pageable) throws ServiceException {
        log.debug("Searching for messages of spots belonging to the category " + messageSearchObject.getCategoryId() + ", not older than: " + messageSearchObject.getTime());

        if (messageSearchObject.getCategoryId() == null) {
            messageSearchObject.setCategoryId(0L);
        }


        if (messageSearchObject.getTime() == null) {
            messageSearchObject.setTime(LocalDateTime.MIN);
        }


        if (messageSearchObject.getHashtagName() != null) {
            String hashtagName = messageSearchObject.getHashtagName();
            Hashtag hashtag = hashtagService.getByName(hashtagName);

            if (hashtag != null){
                List<Message> messageList = hashtag.getMessagesList();
                List<Long> messageIds = new LinkedList<>();

                for (Message m : messageList){
                    messageIds.add(m.getId());
                }

                return messageRepository.filterHash(messageSearchObject.getCategoryId(), messageSearchObject.getTime(), messageIds, pageable);
            } else {
                throw new ServiceException("Invalid hashtag name.");
            }

        }

        return messageRepository.filter(messageSearchObject.getCategoryId(), messageSearchObject.getTime(), pageable);
    }

    private void deleteExpiredMessages() {
        int deletedExpiredMessages = messageRepository.deleteAllByExpirationDateBefore(LocalDateTime.now());
        if (deletedExpiredMessages > 0) {
            log.info("Deleted {} expired messages.");
        }
    }

}
