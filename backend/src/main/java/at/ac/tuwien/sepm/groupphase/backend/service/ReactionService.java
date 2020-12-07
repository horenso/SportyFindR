package at.ac.tuwien.sepm.groupphase.backend.service;

import at.ac.tuwien.sepm.groupphase.backend.entity.Message;
import at.ac.tuwien.sepm.groupphase.backend.entity.Reaction;
import at.ac.tuwien.sepm.groupphase.backend.exception.NotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface ReactionService {

    /**
     * Creates a Reaction Entity
     * @param reaction to be stored
     * @return created Reaction Entity
     */
    Reaction create(Reaction reaction);

    /**
     * Searches all Reactions belonging to a corresponding message
     * @param messageId of the message the reactions belong to
     * @return List of Reactions belonging to that message
     * @throws NotFoundException if the message does not exist in persistence
     */
    List<Reaction> getReactionsByMessageId(Long messageId) throws NotFoundException;
}
