package at.ac.tuwien.sepm.groupphase.backend.repository;

import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.MessageDto;
import at.ac.tuwien.sepm.groupphase.backend.entity.Location;
import at.ac.tuwien.sepm.groupphase.backend.entity.Message;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface MessageRepository extends JpaRepository<Message, Long> {

    /**
     * Find all message entries form one spot ordered by publication date in descending order.
     *
     * @param spotId id of the spot
     * @return ordered list of all message entries
     */
    List<Message> findBySpotIdOrderByPublishedAtDesc(Long spotId);

    /**
     * Find messages that match the filter criteria
     *
     * @param categoryId of spots contained in location
     * @param time ... messages not older than stated time
     * @return List of messages that match the filter criteria
     */
    @EntityGraph("message-with-spots")
    @Query(value = "SELECT DISTINCT m FROM Message m LEFT JOIN Spot s ON s.id = m.spot.id WHERE (s.category.id = :cat OR :cat = 0L) AND m.publishedAt <= :time")
    List<Message> filter(@Param("cat") Long categoryId,
                          @Param("time") LocalDateTime time);
}
