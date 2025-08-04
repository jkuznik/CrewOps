package pl.crewops.domain.auth;

import java.time.LocalDate;
import java.util.Set;
import java.util.UUID;
import pl.crewops.dto.auth.CreateAuthUserDTO;
import pl.crewops.dto.auth.RoleDTO;
import pl.crewops.dto.employee.CreateEmployeeDTO;
import pl.crewops.dto.employee.UpdateEmployeeDTO;
import pl.crewops.model.Employee;
import pl.crewops.model.Machine;
import pl.crewops.model.MachineType;
import pl.crewops.model.Qualification;
import pl.crewops.model.auth.RoleType;

class AuthTestFactory {

    public static CreateAuthUserDTO createAuthUserDTO() {
        return CreateAuthUserDTO.builder()
                .username("username")
                .password("password")
                .roles(Set.of(RoleDTO.builder().name(RoleType.MANAGER.name()).build()))
                .build();
    }

    public static Employee createEmployeeWithQualificationsAndVehicles() {
        return Employee.builder()
                .firstName("firstName")
                .lastName("lastName")
                .birthDate(LocalDate.parse("2000-01-01"))
                .phoneNumber("123456789")
                .department("department")
                .qualifications(getQualifications())
                .machines(getVehicles())
                .build();
    }

    public static Employee createEmployeeWithoutQualificationsAndVehicles() {
        return Employee.builder()
                .firstName("firstName")
                .lastName("lastName")
                .birthDate(LocalDate.parse("2000-01-01"))
                .phoneNumber("123456789")
                .department("department")
                .build();
    }

    public static CreateEmployeeDTO createEmployeeDTO() {
        return CreateEmployeeDTO.builder()
                .firstName("firstName")
                .lastName("lastName")
                .birthDate(LocalDate.parse("2000-01-01"))
                .phoneNumber("123456789")
                .department("department")
                .roles(Set.of())
                .build();
    }

    public static CreateEmployeeDTO createEmployeeDTONotValid() {
        return CreateEmployeeDTO.builder()
                .firstName(null)
                .lastName("lastName")
                .birthDate(LocalDate.parse("2000-01-01"))
                .phoneNumber("123456789")
                .department("department")
                .build();
    }

    public static UpdateEmployeeDTO updateEmployeeDTO() {
        return UpdateEmployeeDTO.builder()
                .employeeId(UUID.fromString("11111111-1111-1111-1111-111111111111"))
                .phoneNumber("123456789")
                .department("department")
                .build();
    }

    public static UpdateEmployeeDTO updateEmployeeDTONotValid() {
        return UpdateEmployeeDTO.builder()
                .employeeId(UUID.fromString("11111111-1111-1111-1111-111111111111"))
                .phoneNumber("123456789andThenMoreChar")
                .department("department")
                .build();
    }

    public static Qualification qualification() {
        return Qualification.builder().description("description").build();
    }

    public static Machine vehicle() {
        return Machine.builder()
                .machineType(MachineType.builder().name("ImplementThis").build())
                .make("make")
                .model("model")
                .year(2020)
                .broken(false)
                .build();
    }

    private static Set<Qualification> getQualifications() {
        return Set.of(
                Qualification.builder().description("foo").build(),
                Qualification.builder().description("bar").build());
    }

    private static Set<Machine> getVehicles() {
        return Set.of(
                vehicle(),
                Machine.builder()
                        .machineType(MachineType.builder().name("ImplementThis").build())
                        .make("make")
                        .model("model")
                        .year(2020)
                        .broken(false)
                        .build());
    }
}
