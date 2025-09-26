package pl.crewops.model.dto.option;

import java.util.UUID;
import lombok.Builder;

@Builder
public record AuthUserOptionDTO(UUID employeeId, UUID optionId, boolean enabled) {}
