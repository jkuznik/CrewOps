package pl.crewops.view.form.model;

import java.time.LocalDate;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.*;
import pl.crewops.auth.RoleDTO;
import pl.crewops.auth.RoleType;
import pl.crewops.dto.employee.CreateEmployeeDTO;
import pl.crewops.dto.employee.EmployeeDTO;
import pl.crewops.dto.employee.UpdateEmployeeDTO;

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
    private Set<RoleType> roles;
    private Set<UUID> qualificationsSet;
    private Set<UUID> vehiclesSet;

    public static EmployeeFormModel toEmployeeFormModel(EmployeeDTO employeeDTO) {
        return EmployeeFormModel.builder()
                .id(employeeDTO.id())
                .firstName(employeeDTO.firstName())
                .lastName(employeeDTO.lastName())
                .birthDate(employeeDTO.birthDate())
                .phoneNumber(employeeDTO.phoneNumber())
                .department(employeeDTO.department())
                .qualificationsSet(employeeDTO.qualifications())
                .vehiclesSet(employeeDTO.vehicles())
                .build();
    }

    public static CreateEmployeeDTO toCreateEmployeeDTO(EmployeeFormModel employeeFormModel) {
        Set<RoleDTO> createRoles = employeeFormModel.roles.stream()
                .map(role -> RoleDTO.builder().name(role.name()).build())
                .collect(Collectors.toSet());

        return CreateEmployeeDTO.builder()
                .firstName(employeeFormModel.getFirstName())
                .lastName(employeeFormModel.getLastName())
                .birthDate(employeeFormModel.getBirthDate())
                .phoneNumber(employeeFormModel.getPhoneNumber())
                .department(employeeFormModel.getDepartment())
                .username(employeeFormModel.firstName.substring(0, 3) + employeeFormModel.lastName.substring(0, 3))
                .password("pass")
                .roles(createRoles)
                .build();
    }

    public static UpdateEmployeeDTO toUpdateEmployeeDTO(EmployeeFormModel employeeFormModel) {
        return UpdateEmployeeDTO.builder()
                .employeeId(employeeFormModel.getId())
                .phoneNumber(employeeFormModel.getPhoneNumber())
                .department(employeeFormModel.getDepartment())
                .build();
    }
}
