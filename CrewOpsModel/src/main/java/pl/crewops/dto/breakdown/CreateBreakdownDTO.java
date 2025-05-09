package pl.crewops.dto.breakdown;

import java.util.UUID;
import lombok.Builder;

@Builder
public record CreateBreakdownDTO(UUID vehicleId, UUID reportedByEmployeeId, String description) {}
