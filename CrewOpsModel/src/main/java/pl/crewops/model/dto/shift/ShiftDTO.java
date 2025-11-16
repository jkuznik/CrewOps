package pl.crewops.model.dto.shift;

import java.util.Set;
import java.util.UUID;
import lombok.Builder;

@Builder
public record ShiftDTO(UUID id, String name, Set<ShiftConfig> shiftConfigs) {}
