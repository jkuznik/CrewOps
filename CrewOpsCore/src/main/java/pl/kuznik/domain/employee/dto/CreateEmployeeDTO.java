package pl.kuznik.domain.employee.dto;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;
import java.util.Set;
import lombok.Builder;
import pl.kuznik.entity.Qualification;
import pl.kuznik.entity.Vehicle;
import pl.kuznik.utils.serializer.QualificationSetSerializer;
import pl.kuznik.utils.serializer.VehicleSetSerializer;

@Builder
public record CreateEmployeeDTO(
        @Size(max = 50) @NotNull @NotBlank String firstName,
        @Size(max = 50) @NotNull @NotBlank String lastName,
        @NotNull LocalDate birthDate,
        @Size(max = 15) String phoneNumber,
        @Size(max = 50) @NotNull @NotBlank String department,
        @JsonSerialize(using = QualificationSetSerializer.class) Set<Qualification> qualifications,
        @JsonSerialize(using = VehicleSetSerializer.class) Set<Vehicle> vehicles) {}
