package at.ac.tuwien.sepm.groupphase.backend.repository;

import at.ac.tuwien.sepm.groupphase.backend.entity.Reaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.List;

@Repository
public interface ReactionRepository extends JpaRepository<Reaction, Long> {

    /**
     * Get all Reactions belonging to a certain message
     *
     * @param messageId of the corresponding message
     * @return List of Reactions belonging to that message
     */
    List<Reaction> getReactionsByMessageId(long messageId);

    List<Reaction> getReactionsByMessage_IdAndType(long messageId, Reaction.ReactionType type);

    @Transactional
    void deleteById(Long reactionId);

    @Transactional
    void deleteAllByMessage_Id(Long messageId);

    Integer countReactionByMessage_IdAndType(Long messageId, Reaction.ReactionType type);

    @Transactional
    @Query("update Reaction r set r.type = :type where r.id = :id")
    List<Reaction> updateReaction(@Param("id") Long reactionId,
                                  @Param("type") Reaction.ReactionType type);
}