package pl.crewops.dto.employee;

import java.time.LocalDate;
import java.util.UUID;
import lombok.Builder;

@Builder
public record EmployeeQualificationDTO(UUID employeeId, UUID qualificationId, LocalDate expiredAt) {}
