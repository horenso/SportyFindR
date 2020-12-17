package at.ac.tuwien.sepm.groupphase.backend.repository;

import at.ac.tuwien.sepm.groupphase.backend.entity.Location;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface LocationRepository extends JpaRepository<Location, Long> {
    /**
     * Finds all location entities in the repository
     *
     * @return list of all location entities
     */
    List<Location> findAll();

    /**
     * Find locations that match the filter criteria
     *
     * @param categoryId of spots contained in location
     * @return List of locations that match the filter criteria
     */
    @Transactional
    @Query(value = "SELECT DISTINCT l FROM Location l LEFT JOIN Spot s ON s.location.id = l.id WHERE s.category.id = :cat")
    List<Location> filter(@Param("cat") Long categoryId);
}
