package at.ac.tuwien.sepm.groupphase.backend.repository;

import at.ac.tuwien.sepm.groupphase.backend.entity.ApplicationUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<ApplicationUser, Long> {

    Optional<ApplicationUser> getOneById(Long userId);

    List<ApplicationUser> findAll();

    List<ApplicationUser> findUsersByRolesId(Long roleId);

    Optional<ApplicationUser> findUsersByEmail(String email);

    // ToDo: should probably be an optional
//    ApplicationUser findUserByEmail(String email);
}
