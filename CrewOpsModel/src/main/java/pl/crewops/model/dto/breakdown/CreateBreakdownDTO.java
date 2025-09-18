package pl.crewops.model.dto.breakdown;

import jakarta.validation.constraints.NotNull;
import java.util.UUID;
import lombok.Builder;

@Builder
public record CreateBreakdownDTO(
        @NotNull UUID machineId,
        @NotNull UUID reportedByEmployeeId,
        @NotNull String description,
        @NotNull boolean critical) {}
