package pl.crewops.model.dto.shift;

import jakarta.validation.constraints.NotNull;
import java.util.Set;
import java.util.UUID;
import lombok.Builder;

@Builder
public record UpdateShiftDTO(@NotNull UUID id, String name, Set<ShiftConfig> shiftConfigs) {}
