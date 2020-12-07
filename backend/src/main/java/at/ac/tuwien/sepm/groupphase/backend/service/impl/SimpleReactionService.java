package at.ac.tuwien.sepm.groupphase.backend.service.impl;

import at.ac.tuwien.sepm.groupphase.backend.entity.Reaction;
import at.ac.tuwien.sepm.groupphase.backend.repository.ReactionRepository;
import at.ac.tuwien.sepm.groupphase.backend.service.ReactionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.lang.invoke.MethodHandles;
import java.time.LocalDateTime;

@Service
public class SimpleReactionService implements ReactionService {

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    private final ReactionRepository reactionRepository;

    public SimpleReactionService(ReactionRepository reactionRepository) {
        this.reactionRepository = reactionRepository;
    }

    @Override
    public Reaction create(Reaction reaction) {
        LOGGER.debug("Create new Reaction {}", reaction);
        reaction.setPublishedAt(LocalDateTime.now());
        return reactionRepository.save(reaction);
    }
}
