package at.ac.tuwien.sepm.groupphase.backend.repository;

import at.ac.tuwien.sepm.groupphase.backend.entity.Message;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MessageRepository extends JpaRepository<Message, Long> {

    /**
     * Find all message entries form one spot ordered by publication date in descending order.
     *
     * @param spotId id of the spot
     * @return ordered list of all message entries
     */
    List<Message> findBySpotIdOrderByPublishedAtAsc(Long spotId);
}
