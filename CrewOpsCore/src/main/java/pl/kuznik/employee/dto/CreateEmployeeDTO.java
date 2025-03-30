package pl.kuznik.employee.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import pl.kuznik.entity.Qualification;
import pl.kuznik.entity.Vehicle;

import java.util.Set;

@Builder
public record CreateEmployeeDTO(
        @Size(max = 50) @NotNull @NotBlank String firstName,
        @Size(max = 50) @NotNull @NotBlank String lastName,
        @Size(max = 15) String phoneNumer,
        @Size(max = 50) @NotNull @NotBlank String department,
        Set<Qualification> qualifications,
        Set<Vehicle> vehicles
) {
    public CreateEmployeeDTO {
    }
}
