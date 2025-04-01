package pl.kuznik.domain.qualification.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;

@Builder
public record CreateQualificationDTO(
        @Size(max = 100) @NotNull @NotBlank String name,
        String description
) {
}
