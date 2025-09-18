package pl.crewops.model.dto.breakdown;

import jakarta.validation.constraints.NotNull;
import java.util.UUID;
import lombok.Builder;

@Builder
public record UpdateBreakdownDTO(@NotNull UUID breakdownId, UUID repairedByEmployeeId, boolean solved) {}
