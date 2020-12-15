package at.ac.tuwien.sepm.groupphase.backend.repository;

import at.ac.tuwien.sepm.groupphase.backend.entity.Reaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

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

    List<Reaction> getReactionsByTypeAndMessageId(String type, long messageId);

    void deleteById(Long reactionId);
}