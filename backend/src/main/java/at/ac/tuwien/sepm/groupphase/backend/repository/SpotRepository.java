package at.ac.tuwien.sepm.groupphase.backend.repository;
import at.ac.tuwien.sepm.groupphase.backend.entity.Spot;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SpotRepository extends JpaRepository<Spot, Long> {


}
