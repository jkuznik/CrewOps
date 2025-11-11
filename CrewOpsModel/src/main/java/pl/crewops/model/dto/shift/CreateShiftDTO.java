package pl.crewops.model.dto.shift;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.Set;
import lombok.Builder;
import pl.crewops.model.dto.jobPosition.JobPositionDTO;

@Builder
public record CreateShiftDTO(
        @NotNull @Size(max = 63) String name, Set<JobPositionDTO> jobPositions, Set<ShiftConfig> configs) {}
