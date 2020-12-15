package at.ac.tuwien.sepm.groupphase.backend.service.impl;

import at.ac.tuwien.sepm.groupphase.backend.entity.Message;
import at.ac.tuwien.sepm.groupphase.backend.entity.Reaction;
import at.ac.tuwien.sepm.groupphase.backend.exception.NotFoundException;
import at.ac.tuwien.sepm.groupphase.backend.repository.MessageRepository;
import at.ac.tuwien.sepm.groupphase.backend.repository.ReactionRepository;
import at.ac.tuwien.sepm.groupphase.backend.service.ReactionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.lang.invoke.MethodHandles;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class SimpleReactionService implements ReactionService {

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    private final ReactionRepository reactionRepository;
    private final MessageRepository messageRepository;

    public SimpleReactionService(ReactionRepository reactionRepository, MessageRepository messageRepository) {
        this.reactionRepository = reactionRepository;
        this.messageRepository = messageRepository;
    }

    @Override
    public Reaction create(Reaction reaction) {
        LOGGER.debug("Create new Reaction {}", reaction);
        reaction.setPublishedAt(LocalDateTime.now());
        return reactionRepository.save(reaction);
    }

    @Override
    public List<Reaction> getReactionsByMessageId(Long messageId) throws NotFoundException {
        LOGGER.debug("Get reactions for message with id {}", messageId);
        Optional<Message> optionalMessage = messageRepository.findById(messageId);
        if (optionalMessage.isEmpty()) {
            throw new NotFoundException("Message with ID " + messageId + " cannot be found!");
        } else {
            return reactionRepository.getReactionsByMessageId(messageId);
        }
    }

    @Override
    public List<Reaction> getThumbsUpByMessageId(Long messageId) {
        LOGGER.debug("Get thumbs up reactions for message with id {}", messageId);
        List<Reaction> thumbsup = getReactionsByMessageId(messageId);
        List<Reaction> result = new ArrayList<>();
        for (Reaction thumbup : thumbsup){
            if (thumbup.getType().equals(Reaction.ReactionType.THUMBS_UP)) {
                result.add(thumbup);
            }
        }
        return result;
    }

    @Override
    public List<Reaction> getThumbsDownByMessageId(Long messageId) {
        LOGGER.debug("Get thumbs down reactions for message with id {}", messageId);
        List<Reaction> thumbsdown = getReactionsByMessageId(messageId);
        List<Reaction> result = new ArrayList<>();
        for (Reaction thumbdown : thumbsdown){
            if (thumbdown.getType().equals(Reaction.ReactionType.THUMBS_DOWN)) {
                result.add(thumbdown);
            }
        }
        return result;
    }

    @Override
    public void deleteAnUpvote(Long messageId) {
        LOGGER.debug("Delete a thumbs up reaction message with id {}", messageId);
        Long reactionId = getThumbsUpByMessageId(messageId).get(0).getId();
        reactionRepository.deleteById(reactionId);
    }

    @Override
    public void deleteADownvote(Long messageId) {
        LOGGER.debug("Delete a thumbs down reaction message with id {}", messageId);
        Long reactionId = getThumbsDownByMessageId(messageId).get(0).getId();
        reactionRepository.deleteById(reactionId);
    }


}
