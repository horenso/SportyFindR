package at.ac.tuwien.sepm.groupphase.backend.repository;

import at.ac.tuwien.sepm.groupphase.backend.entity.ApplicationUser;
import at.ac.tuwien.sepm.groupphase.backend.entity.Reaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


@Repository
@Transactional(readOnly = true)
public interface
ReactionRepository extends JpaRepository<Reaction, Long> {

    /**
     * Get all Reactions belonging to a certain message
     *
     * @param messageId of the corresponding message
     * @return List of Reactions belonging to that message
     */
    List<Reaction> getReactionsByMessageId(long messageId);

    List<Reaction> getReactionsByMessage_IdAndType(long messageId, Reaction.ReactionType type);

    Reaction getOne(Long id);

    @Transactional
    void deleteById(Long reactionId);

    @Transactional
    void deleteAllByMessage_Id(Long messageId);

    @Transactional
    Reaction save(Reaction reaction);

    Integer countReactionByMessage_IdAndType(Long messageId, Reaction.ReactionType type);

    /**
     * Changes a Reaction from one type to another
     *
     * @param reactionId is the Id of the reaction that is being updated
     * @param type       is the type that the reaction is being set to (either thumbs_up or thumbs_down)
     */
    @Transactional
    @Modifying(clearAutomatically = true)
    @Query("update Reaction r set r.type = :type where r.id = :id")
    void updateReaction(@Param("id") Long reactionId,
                        @Param("type") Reaction.ReactionType type);

    void deleteAllByMessage(Long messageId);

    /**
     * Gives a list of reactions from a message that were sent by one person
     *
     * @param ownerId   is the id of the person who reacted to the message
     * @param messageId is the message that the person reacted to
     * @return returns the reactions that were sent by one person on a specific message
     */
    @Transactional
    @Query(value = "SELECT DISTINCT r FROM Reaction r JOIN Message m ON m.id = r.message.id WHERE r.owner.id = :owner_id AND m.id = :message_id")
    List<Reaction> getReactionByOwner(@Param("owner_id") Long ownerId,
                                      @Param("message_id") Long messageId);

    /**
     * Gives a list of reactions from a message that were sent by one person
     *
     * @param ownerEmail is the email of the person who reacted to the message
     * @param messageId  is the message that the person reacted to
     * @return returns the reactions that were sent by one person on a specific message
     */
    @Transactional
    @Query(value = "SELECT DISTINCT r FROM Reaction r JOIN Message m ON m.id = r.message.id JOIN ApplicationUser u ON u.id = r.owner.id WHERE m.id = :message_id AND u.email= :owner_Email")
    List<Reaction> getReactionByOwnerEmail(@Param("owner_Email") String ownerEmail,
                                           @Param("message_id") Long messageId);

    List<Reaction> findByOwner(ApplicationUser owner);
}