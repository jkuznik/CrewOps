package pl.crewops.model;

import static pl.crewops.model.DepartmentFormModel.mapToDepartmentDTOs;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.*;
import pl.crewops.model.auth.RoleType;
import pl.crewops.model.dto.auth.RoleDTO;
import pl.crewops.model.dto.employee.CreateEmployeeDTO;
import pl.crewops.model.dto.employee.EmployeeDTO;
import pl.crewops.model.dto.employee.UpdateEmployeeDTO;
import pl.crewops.model.dto.machine.MachineDTO;
import pl.crewops.model.dto.qualification.QualificationDTO;
import pl.crewops.security.custom.UserPrincipal;

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
    private @Size(max = 63) @Email String email;
    private Set<DepartmentFormModel> departments;
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
                .phoneNumber(employeeDTO.phoneNumber())
                .email(employeeDTO.email())
                .departments(DepartmentFormModel.mapToDepartmentForms(employeeDTO.departments()))
                .qualificationsSet(employeeDTO.qualifications())
                .machinesSet(employeeDTO.machines())
                .roles(Set.copyOf(roles))
                .build();
    }

    public static CreateEmployeeDTO toCreateEmployeeDTO(
            EmployeeFormModel employeeFormModel,
            UserPrincipal principal,
            String translatedSubject,
            String translatedBody) {
        Set<RoleDTO> createRoles = employeeFormModel.roles.stream()
                .map(role -> RoleDTO.builder().name(role.name()).build())
                .collect(Collectors.toSet());

        createRoles.add(new RoleDTO(RoleType.EMPLOYEE.name()));

        CreateEmployeeDTO.NewEmployeeInformation newEmployeeInformation =
                CreateEmployeeDTO.NewEmployeeInformation.builder()
                        .creatorEmployeeId(principal.getEmployeeId())
                        .subject(translatedSubject)
                        .body(translatedBody)
                        .build();

        return CreateEmployeeDTO.builder()
                .newEmployeeInformation(newEmployeeInformation)
                .firstName(employeeFormModel.getFirstName())
                .lastName(employeeFormModel.getLastName())
                .phoneNumber(employeeFormModel.getPhoneNumber())
                .departments(mapToDepartmentDTOs(employeeFormModel.getDepartments()))
                .roles(createRoles)
                .companyId(principal.getCompanyId())
                .build();
    }

    public static UpdateEmployeeDTO toUpdateEmployeeDTO(EmployeeFormModel employeeFormModel) {
        return UpdateEmployeeDTO.builder()
                .employeeId(employeeFormModel.getId())
                .phoneNumber(employeeFormModel.getPhoneNumber())
                .email(employeeFormModel.getEmail())
                .departments(mapToDepartmentDTOs(employeeFormModel.getDepartments()))
                .roles(employeeFormModel.getRoles().stream()
                        .map(roleType -> RoleDTO.builder().name(roleType.name()).build())
                        .collect(Collectors.toSet()))
                .build();
    }
}
