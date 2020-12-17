package at.ac.tuwien.sepm.groupphase.backend.service.impl;

import at.ac.tuwien.sepm.groupphase.backend.entity.Message;
import at.ac.tuwien.sepm.groupphase.backend.entity.Reaction;
import at.ac.tuwien.sepm.groupphase.backend.exception.NotFoundException;
import at.ac.tuwien.sepm.groupphase.backend.repository.MessageRepository;
import at.ac.tuwien.sepm.groupphase.backend.repository.ReactionRepository;
import at.ac.tuwien.sepm.groupphase.backend.service.ReactionService;
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
public class SimpleReactionService implements ReactionService {

    private final ReactionRepository reactionRepository;
    private final MessageRepository messageRepository;
    private final SpotSubscriptionService spotSubscriptionService;

    @Override
    public Reaction create(Reaction reaction) {
        log.debug("Create new Reaction {}", reaction);
        reaction.setPublishedAt(LocalDateTime.now());
        Reaction newReaction = reactionRepository.save(reaction);
        spotSubscriptionService.dispatchMessageWithUpdatedReactions(newReaction.getMessage().getId());
        return newReaction;
    }

    @Override
    public List<Reaction> getReactionsByMessageId(Long messageId) throws NotFoundException {
        log.debug("Get reactions for message with id {}", messageId);
        Optional<Message> optionalMessage = messageRepository.findById(messageId);
        if (optionalMessage.isEmpty()) {
            throw new NotFoundException("Message with ID " + messageId + " cannot be found!");
        } else {
            return reactionRepository.getReactionsByMessageId(messageId);
        }
    }

    @Override
    public void deleteById(Long reactionId) throws NotFoundException {
        Optional<Reaction> reactionOptional = reactionRepository.findById(reactionId);
        if (reactionOptional.isEmpty()) {
            throw new NotFoundException(String.format("Reaction with id %d not found.", reactionId));
        }
        Reaction reaction = reactionOptional.get();
        reactionRepository.deleteById(reactionId);
        spotSubscriptionService.dispatchMessageWithUpdatedReactions(reaction.getMessage().getId());
    }

    @Override
    public Reaction change(Reaction reaction) {
        if (reactionRepository.getOne(reaction.getId()) == null) {
            throw new NotFoundException(String.format("Reaction with id %d not found.", reaction.getId()));
        }
        reactionRepository.updateReaction(reaction.getId(), reaction.getType());
        Reaction newReaction = reactionRepository.getOne(reaction.getId());
        spotSubscriptionService.dispatchMessageWithUpdatedReactions(reaction.getMessage().getId());
        return newReaction;
    }
}
