package pl.crewops.dto.machine;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import pl.crewops.dto.machineType.MachineTypeDTO;

@Builder
public record CreateMachineDTO(
        @Size(max = 31) @NotNull String make,
        @Size(max = 31) @NotNull String model,
        @NotNull MachineTypeDTO machineType,
        @NotNull Integer year,
        @Size(max = 50) String vin,
        @Size(max = 15) String registerNumber,
        @NotNull Boolean broken) {}
