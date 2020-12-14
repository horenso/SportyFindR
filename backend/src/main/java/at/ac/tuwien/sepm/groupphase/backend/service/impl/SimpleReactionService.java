package at.ac.tuwien.sepm.groupphase.backend.service.impl;

import at.ac.tuwien.sepm.groupphase.backend.entity.Message;
import at.ac.tuwien.sepm.groupphase.backend.entity.Reaction;
import at.ac.tuwien.sepm.groupphase.backend.exception.NotFoundException;
import at.ac.tuwien.sepm.groupphase.backend.repository.MessageRepository;
import at.ac.tuwien.sepm.groupphase.backend.repository.ReactionRepository;
import at.ac.tuwien.sepm.groupphase.backend.service.ReactionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.validation.constraints.Null;
import java.lang.invoke.MethodHandles;
import java.time.LocalDateTime;
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
        Message message;
        Optional<Message> optionalMessage = messageRepository.findById(messageId);
        if (optionalMessage.isEmpty()) {
            throw new NotFoundException("Message with ID " + messageId + " cannot be found!");
        } else {
            return reactionRepository.getReactionsByMessageId(messageId);
        }
    }
}
