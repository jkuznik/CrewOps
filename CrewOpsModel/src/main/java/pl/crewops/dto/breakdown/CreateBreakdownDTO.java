package pl.crewops.dto.breakdown;

import jakarta.validation.constraints.NotNull;
import java.util.UUID;
import lombok.Builder;

@Builder
public record CreateBreakdownDTO(
        @NotNull UUID vehicleId,
        @NotNull UUID reportedByEmployeeId,
        @NotNull String description,
        @NotNull boolean critical) {}
