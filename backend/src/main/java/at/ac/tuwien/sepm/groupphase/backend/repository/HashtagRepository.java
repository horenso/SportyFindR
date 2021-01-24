package at.ac.tuwien.sepm.groupphase.backend.repository;

import at.ac.tuwien.sepm.groupphase.backend.entity.Hashtag;
import at.ac.tuwien.sepm.groupphase.backend.entity.Message;
import at.ac.tuwien.sepm.groupphase.backend.entity.Spot;
import org.springframework.data.jpa.repository.JpaRepository;
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
}
