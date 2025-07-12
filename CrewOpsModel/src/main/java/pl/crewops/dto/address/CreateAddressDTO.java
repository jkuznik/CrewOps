package pl.crewops.dto.address;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;

@Builder
public record CreateAddressDTO(
        @Size(max = 31) @NotNull @NotBlank String postalCode,
        @Size(max = 31) @NotNull @NotBlank String city,
        @Size(max = 31) @NotNull @NotBlank String street,
        @Size(max = 31) @NotNull @NotBlank String localNumber) {}
