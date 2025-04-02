package pl.kuznik.domain.employee;

import java.time.LocalDate;
import java.util.Set;
import java.util.UUID;
import pl.kuznik.domain.employee.dto.CreateEmployeeDTO;
import pl.kuznik.domain.employee.dto.UpdateEmployeeDTO;
import pl.kuznik.entity.Employee;
import pl.kuznik.entity.Qualification;
import pl.kuznik.entity.Vehicle;
import pl.kuznik.utils.enums.VehicleType;

class EmployeeTestFactory {

    public static Employee createEmployeeWithQualificationsAndVehicles() {
        return Employee.builder()
                .firstName("firstName")
                .lastName("lastName")
                .birthDate(LocalDate.parse("2000-01-01"))
                .phoneNumber("123456789")
                .department("department")
                .qualifications(createQualifications())
                .vehicles(createVehicles())
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

    public static CreateEmployeeDTO createEmployeeDTOWithQualificationsAndVehicles() {
        return CreateEmployeeDTO.builder()
                .firstName("firstName")
                .lastName("lastName")
                .birthDate(LocalDate.parse("2000-01-01"))
                .phoneNumber("123456789")
                .department("department")
                .qualifications(createQualifications())
                .vehicles(createVehicles())
                .build();
    }

    public static CreateEmployeeDTO createEmployeeDTOWithoutQualificationsAndVehicles() {
        return CreateEmployeeDTO.builder()
                .firstName("firstName")
                .lastName("lastName")
                .birthDate(LocalDate.parse("2000-01-01"))
                .phoneNumber("123456789")
                .department("department")
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
                .employeeId(UUID.randomUUID())
                .phoneNumber("123456789")
                .department("department")
                .build();
    }

    public static UpdateEmployeeDTO updateEmployeeDTONotValid() {
        return UpdateEmployeeDTO.builder()
                .employeeId(UUID.randomUUID())
                .phoneNumber("123456789andThenMoreChar")
                .department("department")
                .build();
    }

    private static Set<Qualification> createQualifications() {
        return Set.of(
                Qualification.builder().description("foo").build(),
                Qualification.builder().description("bar").build());
    }

    private static Set<Vehicle> createVehicles() {
        return Set.of(
                Vehicle.builder()
                        .vehicleType(VehicleType.BULLDOZER)
                        .make("make")
                        .model("model")
                        .year(2020)
                        .broken(false)
                        .build(),
                Vehicle.builder()
                        .vehicleType(VehicleType.EXCAVATOR)
                        .make("make")
                        .model("model")
                        .year(2021)
                        .broken(false)
                        .build());
    }
}
