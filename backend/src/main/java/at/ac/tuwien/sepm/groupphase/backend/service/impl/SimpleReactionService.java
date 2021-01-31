package at.ac.tuwien.sepm.groupphase.backend.service.impl;

import at.ac.tuwien.sepm.groupphase.backend.entity.Message;
import at.ac.tuwien.sepm.groupphase.backend.entity.Reaction;
import at.ac.tuwien.sepm.groupphase.backend.exception.NotFoundException;
import at.ac.tuwien.sepm.groupphase.backend.exception.NotFoundException2;
import at.ac.tuwien.sepm.groupphase.backend.exception.ValidationException;
import at.ac.tuwien.sepm.groupphase.backend.exception.WrongUserException;
import at.ac.tuwien.sepm.groupphase.backend.repository.MessageRepository;
import at.ac.tuwien.sepm.groupphase.backend.repository.ReactionRepository;
import at.ac.tuwien.sepm.groupphase.backend.repository.UserRepository;
import at.ac.tuwien.sepm.groupphase.backend.service.ReactionService;
import at.ac.tuwien.sepm.groupphase.backend.service.SpotSubscriptionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class SimpleReactionService implements ReactionService {

    private final ReactionRepository reactionRepository;
    private final MessageRepository messageRepository;
    private final SpotSubscriptionService spotSubscriptionService;
    private final UserRepository userRepository;

    @Override
    public Reaction create(Reaction reaction) throws NotFoundException2, ValidationException{
        log.debug("Create new Reaction {}", reaction);
        if(messageRepository.findById(reaction.getMessage().getId()).isEmpty()){
            throw new NotFoundException2("Message does not Exist");
        }
        reaction.setPublishedAt(LocalDateTime.now());
        reaction.setOwner(userRepository.findApplicationUserByEmail(SecurityContextHolder.getContext().getAuthentication().getName()).get());
        if(!reactionRepository.getReactionByOwner(reaction.getOwner().getId(),reaction.getMessage().getId()).isEmpty()){
            throw new ValidationException("Already reacted to Message");
        }
        Reaction newReaction = reactionRepository.save(reaction);
        spotSubscriptionService.dispatchMessageWithUpdatedReactions(newReaction.getMessage().getId());
        return newReaction;
    }

    @Override
    public List<Reaction> getReactionsByMessageId(Long messageId) throws NotFoundException2 {
        log.debug("Get reactions for message with id {}", messageId);
        Optional<Message> optionalMessage = messageRepository.findById(messageId);
        if (optionalMessage.isEmpty()) {
            throw new NotFoundException2("Message with ID " + messageId + " cannot be found!");
        } else {
            return reactionRepository.getReactionsByMessageId(messageId);
        }
    }

    @Override
    public void deleteById(Long reactionId) throws NotFoundException2, WrongUserException {
        Optional<Reaction> reactionOptional = reactionRepository.findById(reactionId);
        if (reactionOptional.isEmpty()) {
            throw new NotFoundException2(String.format("Reaction with id %d not found.", reactionId));
        }else if (!reactionOptional.get().getOwner().getEmail().equals(SecurityContextHolder.getContext().getAuthentication().getName())){
            throw new WrongUserException("You can only delete your own messages");
        }
        Reaction reaction = reactionOptional.get();
        reactionRepository.deleteById(reactionId);
        spotSubscriptionService.dispatchMessageWithUpdatedReactions(reaction.getMessage().getId());
    }

    @Override
    public Reaction change(Reaction reaction) throws NotFoundException2, WrongUserException{
        Optional<Reaction> reactionOptional = reactionRepository.findById(reaction.getId());
        if (reactionOptional.isEmpty()) {
            throw new NotFoundException2(String.format("Reaction with id %d not found.", reaction.getId()));
        }else if (!reactionOptional.get().getOwner().getEmail().equals(SecurityContextHolder.getContext().getAuthentication().getName())){
            throw new WrongUserException("You can only delete your own messages");
        }
        reactionRepository.updateReaction(reaction.getId(), reaction.getType());
        spotSubscriptionService.dispatchMessageWithUpdatedReactions(reaction.getMessage().getId());
        return reaction;
    }
}
