package pl.crewops.model.dto.qualification;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.UUID;
import lombok.Builder;

@Builder
public record UpdateQualificationDTO(@NotNull UUID qualificationId, @NotNull @NotBlank String description) {}
