package pl.crewops.domain.employee;

import java.time.LocalDate;
import java.util.Set;
import java.util.UUID;
import pl.crewops.IntegrationTest;
import pl.crewops.dto.employee.CreateEmployeeDTO;
import pl.crewops.dto.employee.EmployeeDTO;
import pl.crewops.dto.employee.UpdateEmployeeDTO;
import pl.crewops.model.Employee;
import pl.crewops.model.Qualification;
import pl.crewops.model.Vehicle;
import pl.crewops.model.VehicleType;

class EmployeeTestFactory {

    static final UUID employeeId = UUID.fromString("11111111-1111-1111-1111-111111111111");

    static Employee employeeWithQualificationsAndVehicles() {
        return Employee.builder()
                .firstName("firstName")
                .lastName("lastName")
                .birthDate(LocalDate.parse("2000-01-01"))
                .phoneNumber("123456789")
                .department("department")
                .qualifications(getQualifications())
                .vehicles(getVehicles())
                .active(true)
                .build();
    }

    static Employee employeeWithoutQualificationsAndVehicles() {
        return Employee.builder()
                .firstName("firstName")
                .lastName("lastName")
                .birthDate(LocalDate.parse("2000-01-01"))
                .phoneNumber("123456789")
                .department("department")
                .active(true)
                .build();
    }

    static EmployeeDTO employeeDTO() {
        return EmployeeDTO.builder()
                .id(employeeId)
                .firstName("firstName")
                .lastName("lastName")
                .birthDate(LocalDate.parse("2000-01-01"))
                .phoneNumber("123456789")
                .department("department")
                .build();
    }

    static CreateEmployeeDTO createEmployeeDTO() {
        return CreateEmployeeDTO.builder()
                .firstName("firstName")
                .lastName("lastName")
                .birthDate(LocalDate.parse("2000-01-01"))
                .phoneNumber("123456789")
                .department("department")
                .username("username")
                .password("password")
                .roles(Set.of())
                .tenantName(IntegrationTest.TEST_TENANT_NAME)
                .build();
    }

    static CreateEmployeeDTO createEmployeeDTONotValid() {
        return CreateEmployeeDTO.builder()
                .firstName(null)
                .lastName("lastName")
                .birthDate(LocalDate.parse("2000-01-01"))
                .phoneNumber("123456789")
                .department("department")
                .build();
    }

    static UpdateEmployeeDTO updateEmployeeDTO() {
        return UpdateEmployeeDTO.builder()
                .employeeId(employeeId)
                .phoneNumber("123456789")
                .department("department")
                .build();
    }

    static UpdateEmployeeDTO updateEmployeeDTONotValid() {
        return UpdateEmployeeDTO.builder()
                .employeeId(employeeId)
                .phoneNumber("123456789andThenMoreChar")
                .department("department")
                .build();
    }

    static Qualification qualification() {
        return Qualification.builder().description("description").build();
    }

    static Vehicle vehicle() {
        return Vehicle.builder()
                .vehicleType(VehicleType.builder().name("ImplementThis").build())
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
                        .vehicleType(VehicleType.builder().name("ImplementThis").build())
                        .make("make")
                        .model("model")
                        .year(2020)
                        .broken(false)
                        .build());
    }
}
