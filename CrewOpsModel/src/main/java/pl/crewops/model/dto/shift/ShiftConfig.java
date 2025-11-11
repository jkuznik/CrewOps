package pl.crewops.model.dto.shift;

import java.util.UUID;
import lombok.Builder;

@Builder
public record ShiftConfig(UUID jopPositionId, UUID relatedEmployeeId, boolean critical) {}
