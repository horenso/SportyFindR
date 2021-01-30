package at.ac.tuwien.sepm.groupphase.backend.service;

import at.ac.tuwien.sepm.groupphase.backend.entity.Reaction;
import at.ac.tuwien.sepm.groupphase.backend.exception.NotFoundException;
import at.ac.tuwien.sepm.groupphase.backend.exception.NotFoundException2;
import at.ac.tuwien.sepm.groupphase.backend.exception.WrongUserException;

import java.util.List;

public interface ReactionService {

    /**
     * Creates a Reaction Entity
     *
     * @param reaction to be stored
     * @return created Reaction Entity
     */
    Reaction create(Reaction reaction) throws NotFoundException2;

    /**
     * Searches all Reactions belonging to a corresponding message
     *
     * @param messageId of the message the reactions belong to
     * @return List of Reactions belonging to that message
     * @throws NotFoundException if the message does not exist in persistence
     */
    List<Reaction> getReactionsByMessageId(Long messageId) throws NotFoundException2;

    void deleteById(Long reactionId) throws NotFoundException2, WrongUserException;

    Reaction change(Reaction reaction) throws NotFoundException2, WrongUserException;

    /**
     * Returns all Reactions that belong to a user
     * @param userId id of the owning user
     * @return a list of Reactions belonging to the user
     * @throws NotFoundException2 if the user cannot be found
     */
    List<Reaction> findReactionsByOwner(Long userId) throws NotFoundException2;
}
