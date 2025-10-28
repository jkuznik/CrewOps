package pl.crewops.domain.jobPosition;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import pl.crewops.model.tenantSchema.JobPosition;

interface JobPositionRepository extends JpaRepository<JobPosition, UUID> {

    @Query("SELECT jp FROM JobPosition jp ORDER BY jp.name ASC")
    List<JobPosition> findAll();

    Optional<JobPosition> findByName(String name);
}
