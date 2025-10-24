package pl.crewops.domain.jobPosition;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.validation.annotation.Validated;
import pl.crewops.model.dto.jobPosition.CreateJobPositionDTO;
import pl.crewops.model.dto.jobPosition.JobPositionDTO;
import pl.crewops.model.dto.jobPosition.UpdateJobPositionDTO;
import pl.crewops.model.tenantSchema.JobPosition;

@Validated
public interface JobPositionAPI {

    JobPositionDTO createJobPosition(@NotNull @Valid CreateJobPositionDTO createJobPositionDTO);

    Optional<JobPosition> findById(@NotNull UUID id);

    Optional<JobPosition> findByName(@NotNull String name);

    List<JobPositionDTO> getAllJobPositions();

    JobPositionDTO updateJobPosition(@NotNull @Valid UpdateJobPositionDTO updateJopPositionDTO);

    void deleteById(@NotNull UUID id);
}
