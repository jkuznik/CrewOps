package pl.crewops.domain.auth;

import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import pl.crewops.model.auth.AuthUser;

@Repository
interface AuthUserRepository extends JpaRepository<AuthUser, UUID> {

    @Query("SELECT u FROM AuthUser u JOIN FETCH u.roles WHERE u.username = :username")
    Optional<AuthUser> findByUsername(String username);
}
