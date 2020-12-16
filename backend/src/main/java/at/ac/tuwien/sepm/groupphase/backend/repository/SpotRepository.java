package at.ac.tuwien.sepm.groupphase.backend.repository;

import at.ac.tuwien.sepm.groupphase.backend.entity.Location;
import at.ac.tuwien.sepm.groupphase.backend.entity.Spot;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SpotRepository extends JpaRepository<Spot, Long> {

    @Query(value = "SELECT DISTINCT l FROM Location l JOIN Spot s ON l.id = s.location.id " +
        "WHERE l.id = :locationId")
    List<Location> findLocationWithSpot(@Param("locationId") Long locationId);

    List<Spot> getSpotsByLocationId(Long locationId);
}
