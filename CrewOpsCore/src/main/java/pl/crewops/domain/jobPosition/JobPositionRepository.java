package pl.crewops.domain.jobPosition;

import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import pl.crewops.model.tenantSchema.JobPosition;

interface JobPositionRepository extends JpaRepository<JobPosition, UUID> {}
