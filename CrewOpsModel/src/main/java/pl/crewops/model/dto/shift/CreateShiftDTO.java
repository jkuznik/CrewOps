package pl.crewops.model.dto.shift;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.Set;
import lombok.Builder;

@Builder
public record CreateShiftDTO(
        @NotNull @Size(max = 63) String name, Set<ShiftConfig> configs, @NotNull @Size(max = 63) String color) {}
