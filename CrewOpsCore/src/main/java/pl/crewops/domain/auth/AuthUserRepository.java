package pl.crewops.domain.auth;

import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import pl.crewops.model.publicSchema.AuthUser;
import pl.crewops.model.publicSchema.Role;

@Repository
interface AuthUserRepository extends JpaRepository<AuthUser, UUID> {

    @EntityGraph(attributePaths = {"roles"})
    Optional<AuthUser> findByUsername(String username);

    Optional<AuthUser> findByEmployeeId(UUID employeeId);

    @Modifying
    @Query("DELETE FROM AuthUser WHERE id = :id")
    void deleteById(UUID id);

    @Modifying
    @Query("DELETE FROM AuthUser WHERE employeeId =:employeeId")
    void deleteByEmployeeId(UUID employeeId);
}

@Repository
interface RoleRepository extends JpaRepository<Role, UUID> {
    Optional<Role> findById(UUID id);

    Optional<Role> findByName(String name);
}
