package pl.crewops.domain.auth;

import java.time.LocalDate;
import java.util.Set;
import java.util.UUID;
import pl.crewops.auth.CreateAuthUserDTO;
import pl.crewops.auth.RoleDTO;
import pl.crewops.dto.employee.CreateEmployeeDTO;
import pl.crewops.dto.employee.UpdateEmployeeDTO;
import pl.crewops.model.Employee;
import pl.crewops.model.Qualification;
import pl.crewops.model.Vehicle;
import pl.crewops.model.VehicleType;
import pl.crewops.model.auth.RoleType;

public class AuthTestFactory {

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
                .vehicles(getVehicles())
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
                .username("username")
                .password("password")
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

    public static Vehicle vehicle() {
        return Vehicle.builder()
                .vehicleType(VehicleType.builder().type("ImplementThis").build())
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

    private static Set<Vehicle> getVehicles() {
        return Set.of(
                vehicle(),
                Vehicle.builder()
                        .vehicleType(VehicleType.builder().type("ImplementThis").build())
                        .make("make")
                        .model("model")
                        .year(2020)
                        .broken(false)
                        .build());
    }
}
