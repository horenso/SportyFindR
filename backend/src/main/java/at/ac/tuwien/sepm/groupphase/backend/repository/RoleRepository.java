package at.ac.tuwien.sepm.groupphase.backend.repository;

import at.ac.tuwien.sepm.groupphase.backend.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {

    List<Role> findAll();

    Optional<Role> findRoleByName(String roleName);

    Optional<Role> findRoleById(Long roleId);

    List<Role> findRolesByApplicationUsersId(Long applicationUserId);

}
