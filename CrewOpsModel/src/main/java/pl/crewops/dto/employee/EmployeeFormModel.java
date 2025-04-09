package pl.crewops.dto.employee;

import java.time.LocalDate;
import java.util.Set;
import java.util.UUID;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class EmployeeFormModel {
    private String firstName;
    private String lastName;
    private LocalDate birthDate;
    private String phoneNumber;
    private String department;
    private Set<UUID> qualifications;
    private Set<UUID> vehicles;

    public static CreateEmployeeDTO toCreateEmployeeDTO(EmployeeFormModel employeeFormModel) {
        return CreateEmployeeDTO.builder()
                .firstName(employeeFormModel.getFirstName())
                .lastName(employeeFormModel.getLastName())
                .birthDate(employeeFormModel.getBirthDate())
                .phoneNumber(employeeFormModel.getPhoneNumber())
                .department(employeeFormModel.getDepartment())
                .build();
    }
}
