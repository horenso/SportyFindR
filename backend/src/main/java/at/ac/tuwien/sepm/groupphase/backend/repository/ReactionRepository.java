package at.ac.tuwien.sepm.groupphase.backend.repository;

import at.ac.tuwien.sepm.groupphase.backend.entity.Reaction;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


@Repository
@Transactional(readOnly = true)
public interface ReactionRepository extends JpaRepository<Reaction, Long> {

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


    @Transactional
    @Modifying(clearAutomatically = true)
    @Query("update Reaction r set r.type = :type where r.id = :id")
    void updateReaction(@Param("id") Long reactionId,
                        @Param("type") Reaction.ReactionType type);

    void deleteAllByMessage(Long messageId);
}