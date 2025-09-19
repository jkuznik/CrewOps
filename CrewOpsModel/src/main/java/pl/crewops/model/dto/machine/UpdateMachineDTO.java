package pl.crewops.model.dto.machine;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.UUID;
import lombok.Builder;

@Builder
public record UpdateMachineDTO(@NotNull UUID machineId, @Size(max = 15) String registerNumber, Boolean broken) {}
