package at.ac.tuwien.sepm.groupphase.backend.repository;

import at.ac.tuwien.sepm.groupphase.backend.entity.ApplicationUser;
import at.ac.tuwien.sepm.groupphase.backend.entity.Message;
import at.ac.tuwien.sepm.groupphase.backend.exception.NotFoundException;
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
     * Find all message ids form one spot ordered by publication date in ascending order.
     *
     * @param spotId id of the spot
     * @return ordered list of all message ids
     */
    @Query(value = "SELECT DISTINCT m.id FROM Message m JOIN Spot s ON s.id = m.spot.id WHERE s.id = :spotId")
    List<Long> findBySpotIdOrderByPublishedAtAscLong(@Param("spotId") Long spotId);

    Optional<Message> findById(Long id);

    @Transactional
    void deleteById(Long id);

    /**
     * Find messages that match the filter criteria
     *
     * @param categoryId of spots contained in location
     * @param time       ... messages not older than stated time
     * @param user       ... with the owner of the message equaling the stated user
     * @return List of messages that match the filter criteria
     */
    @EntityGraph("message-with-spots-and-owner")
    @Query(value = "SELECT DISTINCT m FROM Message m LEFT JOIN Spot s ON s.id = m.spot.id WHERE (s.category.id = :cat OR :cat = 0L) AND (m.owner.name LIKE :user OR :user LIKE '0') AND m.publishedAt >= :time")
    Page<Message> filter(@Param("cat") Long categoryId,
                         @Param("user") String user,
                         @Param("time") LocalDateTime time,
                         Pageable pageable);

    /**
     * Find messages that match the filter criteria
     *
     * @param categoryId of spots contained in location
     * @param user       ... with the owner of the message equaling the stated user
     * @param time       ... messages not older than stated time
     * @param hashtag    ... is the name of the hashtag that is being looked for
     * @return Page of messages that match the filter criteria
     */

    @EntityGraph("message-with-spots-and-owner")
    @Query(value = "SELECT DISTINCT m FROM Message m LEFT JOIN Spot s ON s.id = m.spot.id JOIN m.hashtagList h WHERE (s.category.id = :cat OR :cat = 0L) AND (m.owner.name LIKE :user OR :user = '0') AND m.publishedAt >= :time AND h.name=:hashtag")
    Page<Message> filterHash(@Param("cat") Long categoryId,
                             @Param("user") String user,
                             @Param("time") LocalDateTime time,
                             @Param("hashtag") String hashtag,
                             Pageable pageable);

    List<Message> findAllBySpot_Id(Long spotId);

    Page<Message> findByIdIn(List<Long> ids, Pageable pageable);

    /**
     * finds all Messages owned by the user
     *
     * @param user that owns the messages
     * @return List of messages owned by that user
     * @throws NotFoundException if the User cannot be
     */
    List<Message> findByOwner(ApplicationUser user) throws NotFoundException;

    @Transactional
    List<Message> deleteAllByExpirationDateBefore(LocalDateTime time);

    Page<Message> findAllBySpotId(Long id, Pageable pageable);
}
