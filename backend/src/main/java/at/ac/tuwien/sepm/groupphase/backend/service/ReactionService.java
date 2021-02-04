package at.ac.tuwien.sepm.groupphase.backend.service;

import at.ac.tuwien.sepm.groupphase.backend.entity.Reaction;
import at.ac.tuwien.sepm.groupphase.backend.exception.NotFoundException2;
import at.ac.tuwien.sepm.groupphase.backend.exception.ValidationException;
import at.ac.tuwien.sepm.groupphase.backend.exception.WrongUserException;

import java.util.List;

public interface ReactionService {

    /**
     * Creates a Reaction Entity
     *
     * @param reaction to be stored
     * @return created Reaction Entity
     * @throws NotFoundException2  if the message that is being reacted to does not exist
     * @throws ValidationException if the user already reacted to this message
     */
    Reaction create(Reaction reaction) throws NotFoundException2, ValidationException;

    /**
     * Searches all Reactions belonging to a corresponding message
     *
     * @param messageId of the message the reactions belong to
     * @return List of Reactions belonging to that message
     * @throws NotFoundException2 if the message does not exist in persistence
     */
    List<Reaction> getReactionsByMessageId(Long messageId) throws NotFoundException2;

    /**
     * deleted a reaction by its id
     *
     * @param reactionId the id of the reaction that is being deleted
     * @throws NotFoundException2 if the reaction does not exist
     * @throws WrongUserException if the wrong user is trying to delete the reaction
     */

    void deleteById(Long reactionId) throws NotFoundException2, WrongUserException;

    /**
     * change a reaction from one type to another
     *
     * @param reaction the reaction that is being changed
     * @return the changed reaction
     * @throws NotFoundException2 if the reaction does not exist
     * @throws WrongUserException if the wrong user is trying to change the reaction
     */
    Reaction change(Reaction reaction) throws NotFoundException2, WrongUserException;

    /**
     * Returns all Reactions that belong to a user
     *
     * @param userId id of the owning user
     * @return a list of Reactions belonging to the user
     * @throws NotFoundException2 if the user cannot be found
     */
    List<Reaction> findReactionsByOwner(Long userId) throws NotFoundException2;
}
