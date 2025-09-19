package pl.crewops.domain.registration;

import java.time.Instant;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import pl.crewops.model.publicSchema.Registration;

interface RegistrationRepository extends JpaRepository<Registration, UUID> {

    @Modifying(clearAutomatically = true)
    @Query(
            """
        UPDATE Registration r
        SET r.status = 'EXPIRED'
        WHERE r.status = 'PENDING'
          AND r.createdAt < :threshold
    """)
    int expirePendingRegistrations(@Param("threshold") Instant threshold);
}
