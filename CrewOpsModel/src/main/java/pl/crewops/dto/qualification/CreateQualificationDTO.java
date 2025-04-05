package pl.crewops.dto.qualification;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

@Builder
public record CreateQualificationDTO(@NotNull @NotBlank String description) {}
