package pl.crewops.view.model;

import java.time.LocalDate;
import java.util.UUID;
import lombok.*;
import pl.crewops.dto.employee.CreateEmployeeDTO;
import pl.crewops.dto.employee.EmployeeDTO;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EmployeeFormModel {
    private UUID id;
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
