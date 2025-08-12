package pl.crewops.model;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.*;
import pl.crewops.dto.auth.RoleDTO;
import pl.crewops.dto.employee.CreateEmployeeDTO;
import pl.crewops.dto.employee.EmployeeDTO;
import pl.crewops.dto.employee.UpdateEmployeeDTO;
import pl.crewops.dto.machine.MachineDTO;
import pl.crewops.dto.qualification.QualificationDTO;
import pl.crewops.model.auth.RoleType;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EmployeeFormModel {
    private UUID id;
    private @NotNull @Size(min = 2, max = 50, message = "Minimal length is 2") String firstName;
    private @NotNull @Size(min = 2, max = 50, message = "Minimal length is 2") String lastName;
    private @NotNull(message = "This field can't be empty") LocalDate birthDate;
    private @Size(max = 15, message = "Max length is 15") String phoneNumber;
    private @NotNull @Size(min = 2, max = 50, message = "Minimal length is 2") String department;
    private Set<RoleType> roles;
    private Set<QualificationDTO> qualificationsSet;
    private Set<MachineDTO> machinesSet;

    public static EmployeeFormModel toEmployeeFormModel(EmployeeDTO employeeDTO) {
        Set<RoleType> roles = new HashSet<>();
        if (employeeDTO != null && employeeDTO.roles() != null) {
            roles = employeeDTO.roles().stream()
                    .map(roleDTO -> RoleType.valueOf(roleDTO.name()))
                    .collect(Collectors.toSet());
        }

        return EmployeeFormModel.builder()
                .id(employeeDTO.id())
                .firstName(employeeDTO.firstName())
                .lastName(employeeDTO.lastName())
                .birthDate(employeeDTO.birthDate())
                .phoneNumber(employeeDTO.phoneNumber())
                .department(employeeDTO.department())
                .qualificationsSet(employeeDTO.qualifications())
                .machinesSet(employeeDTO.machines())
                .roles(Set.copyOf(roles))
                .build();
    }

    public static CreateEmployeeDTO toCreateEmployeeDTO(EmployeeFormModel employeeFormModel, UUID companyId) {
        Set<RoleDTO> createRoles = employeeFormModel.roles.stream()
                .map(role -> RoleDTO.builder().name(role.name()).build())
                .collect(Collectors.toSet());

        createRoles.add(new RoleDTO(RoleType.EMPLOYEE.name()));

        return CreateEmployeeDTO.builder()
                .firstName(employeeFormModel.getFirstName())
                .lastName(employeeFormModel.getLastName())
                .birthDate(employeeFormModel.getBirthDate())
                .phoneNumber(employeeFormModel.getPhoneNumber())
                .department(employeeFormModel.getDepartment())
                .roles(createRoles)
                .companyId(companyId)
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
