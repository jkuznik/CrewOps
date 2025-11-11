package pl.crewops.model.dto.shift;

import java.util.Set;
import java.util.UUID;
import lombok.Builder;
import pl.crewops.model.dto.jobPosition.JobPositionDTO;

@Builder
public record ShiftDTO(UUID id, String name, Set<JobPositionDTO> jobPositions) {}
