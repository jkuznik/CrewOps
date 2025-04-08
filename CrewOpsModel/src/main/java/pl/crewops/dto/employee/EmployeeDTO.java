package pl.crewops.dto.employee;

import java.time.LocalDate;
import java.util.Set;
import java.util.UUID;
import lombok.Builder;

@Builder
public record EmployeeDTO(
        UUID id,
        String firstName,
        String lastName,
        // TODO: modify birth date type if needed
        LocalDate birthDate,
        String phoneNumber,
        String department,
        Set<UUID> qualifications,
        Set<UUID> vehicles) {}
