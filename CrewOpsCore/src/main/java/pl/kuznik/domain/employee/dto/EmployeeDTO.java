package pl.kuznik.domain.employee.dto;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import java.time.LocalDate;
import java.util.Set;
import java.util.UUID;
import lombok.Builder;
import pl.kuznik.entity.Qualification;
import pl.kuznik.entity.Vehicle;
import pl.kuznik.utils.serializer.QualificationSetSerializer;
import pl.kuznik.utils.serializer.VehicleSetSerializer;

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
