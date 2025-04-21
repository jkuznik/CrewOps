package pl.crewops.domain.auth;

import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pl.crewops.model.auth.AuthUser;

@Repository
interface AuthUserRepository extends JpaRepository<AuthUser, UUID> {

    @EntityGraph(attributePaths = "employee")
    Optional<AuthUser> findByUsername(String username);
}
