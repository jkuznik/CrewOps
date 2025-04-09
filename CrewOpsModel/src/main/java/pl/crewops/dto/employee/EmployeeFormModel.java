package pl.crewops.dto.employee;

import java.time.LocalDate;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EmployeeFormModel {
    private String firstName;
    private String lastName;
    private LocalDate birthDate;
    private String phoneNumber;
    private String department;

    public static EmployeeFormModel toEmployeeFormModel(EmployeeDTO employeeDTO) {
        return EmployeeFormModel.builder()
                .firstName(employeeDTO.firstName())
                .lastName(employeeDTO.lastName())
                .birthDate(employeeDTO.birthDate())
                .phoneNumber(employeeDTO.phoneNumber())
                .department(employeeDTO.department())
                .build();
    }

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
