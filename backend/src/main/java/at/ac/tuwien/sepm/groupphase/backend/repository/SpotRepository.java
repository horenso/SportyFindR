package at.ac.tuwien.sepm.groupphase.backend.repository;

import at.ac.tuwien.sepm.groupphase.backend.entity.ApplicationUser;
import at.ac.tuwien.sepm.groupphase.backend.entity.Location;
import at.ac.tuwien.sepm.groupphase.backend.entity.Spot;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
public interface SpotRepository extends JpaRepository<Spot, Long> {

    Optional<Spot> getOneById(Long spotId);

    /**
     * gives a location with a certain Id if it contains a spot
     *
     * @param locationId is the id of the location that will be returned (if it has at least one spot)
     * @return a location with id locationId if it contains a spot
     */
    @Transactional
    @Query(value = "SELECT DISTINCT l FROM Location l JOIN Spot s ON l.id = s.location.id " +
        "WHERE l.id = :locationId")
    List<Location> findLocationWithSpot(@Param("locationId") Long locationId);

    List<Spot> getSpotsByLocationId(Long locationId);

    @Transactional
    void deleteById(Long id);

    List<Spot> findByOwner(ApplicationUser owner);
}
