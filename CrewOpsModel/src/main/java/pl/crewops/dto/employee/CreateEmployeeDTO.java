package pl.crewops.dto.employee;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;
import java.util.Set;
import lombok.Builder;
import pl.crewops.model.Qualification;
import pl.crewops.model.Vehicle;
import pl.crewops.utils.serializer.QualificationSetSerializer;
import pl.crewops.utils.serializer.VehicleSetSerializer;

@Builder
public record CreateEmployeeDTO(
        @Size(max = 50) @NotNull @NotBlank String firstName,
        @Size(max = 50) @NotNull @NotBlank String lastName,
        @NotNull LocalDate birthDate,
        @Size(max = 15) String phoneNumber,
        @Size(max = 50) @NotNull @NotBlank String department,
        @JsonSerialize(using = QualificationSetSerializer.class) Set<Qualification> qualifications,
        @JsonSerialize(using = VehicleSetSerializer.class) Set<Vehicle> vehicles) {}
