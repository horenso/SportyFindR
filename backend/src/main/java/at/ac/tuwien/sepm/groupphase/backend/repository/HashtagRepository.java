package at.ac.tuwien.sepm.groupphase.backend.repository;

import at.ac.tuwien.sepm.groupphase.backend.entity.Hashtag;
import at.ac.tuwien.sepm.groupphase.backend.entity.Message;
import at.ac.tuwien.sepm.groupphase.backend.entity.Spot;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface HashtagRepository extends JpaRepository<Hashtag, Long> {

    Optional<Hashtag> findHashtagByName(String name);

    List<Hashtag> findHashtagsByMessagesListContains(Message message);

    List<Hashtag> findHashtagsBySpotsListContains(Spot spot);

    Hashtag getHashtagByNameEquals(String name);

    Hashtag getOneById(Long id);

    /**
     * Find hashtag with corresponding name
     *
     * @param name of hashtag
     * @return Hashtag that matches the name
     */
    @Query(value = "SELECT h FROM Hashtag h WHERE LOWER(h.name) LIKE LOWER(CONCAT('%',:name,'%'))")
    List<Hashtag> searchByName(@Param("name") String name);

}
