package pl.crewops.model.dto.company;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;

@Builder
public record CreateCompanyDTO(
        @Size(max = 63) @NotNull @NotBlank String name,
        @NotNull String taxId,
        @Email @NotNull @NotBlank String email) {}
