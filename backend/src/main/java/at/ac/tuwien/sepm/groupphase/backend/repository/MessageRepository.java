package at.ac.tuwien.sepm.groupphase.backend.repository;

import at.ac.tuwien.sepm.groupphase.backend.entity.Message;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface MessageRepository extends JpaRepository<Message, Long> {

    /**
     * Find all message entries form one spot ordered by publication date in ascending order.
     *
     * @param spotId id of the spot
     * @return ordered list of all message entries
     */
    List<Message> findBySpotIdOrderByPublishedAtAsc(Long spotId);

    /**
     * Find all message entries form one spot ordered by publication date in ascending order.
     *
     * @param spotId id of the spot
     * @return ordered list of all message entries
     */
    @Query(value = "SELECT DISTINCT m.id FROM Message m LEFT JOIN Spot s ON :spotId = m.spot.id")
    List<Long> findBySpotIdOrderByPublishedAtAscLong(@Param("spotId") Long spotId);

    Optional<Message> findById(Long id);

    @Transactional
    void deleteById(Long id);

    /**
     * Find messages that match the filter criteria
     *
     * @param categoryId of spots contained in location
     * @param time ... messages not older than stated time
     * @return List of messages that match the filter criteria
     */
    @EntityGraph("message-with-spots")
    @Query(value = "SELECT DISTINCT m FROM Message m LEFT JOIN Spot s ON s.id = m.spot.id WHERE (s.category.id = :cat OR :cat = 0L) AND m.publishedAt <= :time")
    Page<Message> filter(@Param("cat") Long categoryId,
                         @Param("time") LocalDateTime time,
                         Pageable pageable);

    /**
     * Find messages that match the filter criteria
     *
     * @param categoryId of spots contained in location
     * @param time ... messages not older than stated time
     * @param messageIds ... list of messages after hashtag check
     * @return List of messages that match the filter criteria
     */
    @EntityGraph("message-with-spots")
    @Query(value = "SELECT DISTINCT m FROM Message m LEFT JOIN Spot s ON s.id = m.spot.id WHERE (s.category.id = :cat OR :cat = 0L) AND m.publishedAt <= :time AND m.id IN :list")
    Page<Message> filterHash(@Param("cat") Long categoryId,
                             @Param("time") LocalDateTime time,
                             @Param("list") List<Long> messageIds,
                             Pageable pageable);

    List<Message> findAllBySpot_Id(Long spotId);

    Page<Message> findByIdIn(List<Long> ids, Pageable pageable);

}
