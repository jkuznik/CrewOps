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
        Set<UUID> vehicles) {

    public static CreateEmployeeDTO toCreateEmployeeDTO(EmployeeDTO thereIsAIssue) {
        return CreateEmployeeDTO.builder()
                .firstName(thereIsAIssue.firstName)
                .lastName(thereIsAIssue.lastName)
                .birthDate(thereIsAIssue.birthDate)
                .phoneNumber(thereIsAIssue.phoneNumber)
                .department(thereIsAIssue.department)
                .build();
    }
}
