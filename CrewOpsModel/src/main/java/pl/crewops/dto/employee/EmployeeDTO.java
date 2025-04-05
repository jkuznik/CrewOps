package pl.crewops.dto.employee;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import java.time.LocalDate;
import java.util.Set;
import java.util.UUID;
import lombok.Builder;
import pl.crewops.model.Qualification;
import pl.crewops.model.Vehicle;
import pl.crewops.utils.serializer.QualificationSetSerializer;
import pl.crewops.utils.serializer.VehicleSetSerializer;

@Builder
public record EmployeeDTO(
        UUID id,
        String firstName,
        String lastName,
        LocalDate birthDate,
        String phoneNumber,
        String department,
        @JsonSerialize(using = QualificationSetSerializer.class) Set<Qualification> qualifications,
        @JsonSerialize(using = VehicleSetSerializer.class) Set<Vehicle> vehicles) {}
