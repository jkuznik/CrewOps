package pl.crewops.model.dto.option;

import java.util.UUID;
import lombok.Builder;

@Builder
public record OptionDTO(UUID id, String name, String description) {}
