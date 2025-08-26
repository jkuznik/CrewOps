package pl.crewops.domain.employee;

import java.time.LocalDate;
import java.util.Set;
import java.util.UUID;
import pl.crewops.IntegrationTest;
import pl.crewops.dto.department.DepartmentDTO;
import pl.crewops.dto.employee.CreateEmployeeDTO;
import pl.crewops.dto.employee.UpdateEmployeeDTO;
import pl.crewops.model.*;

class EmployeeTestFactory {

    static final UUID employeeId = UUID.fromString("11111111-1111-1111-1111-111111111111");

    static Employee employeeWithQualificationsAndMachines() {
        return Employee.builder()
                .firstName("firstName")
                .lastName("lastName")
                .birthDate(LocalDate.parse("2000-01-01"))
                .phoneNumber("123456789")
                .departments(departments())
                .qualifications(getQualifications())
                .machines(getMachines())
                .active(true)
                .build();
    }

    static Employee employeeWithoutQualificationsAndMachines() {
        return Employee.builder()
                .firstName("firstName")
                .lastName("lastName")
                .birthDate(LocalDate.parse("2000-01-01"))
                .phoneNumber("123456789")
                .departments(departments())
                .active(true)
                .build();
    }

    static CreateEmployeeDTO createEmployeeDTO() {
        return CreateEmployeeDTO.builder()
                .firstName("firstName")
                .lastName("lastName")
                .birthDate(LocalDate.parse("2000-01-01"))
                .phoneNumber("123456789")
                .departments(departmentsDTOs())
                .roles(Set.of())
                .companyId(IntegrationTest.TEST_TENANT_COMPANY_ID)
                .build();
    }

    static CreateEmployeeDTO createEmployeeDTONotValid() {
        return CreateEmployeeDTO.builder()
                .firstName(null)
                .lastName("lastName")
                .birthDate(LocalDate.parse("2000-01-01"))
                .phoneNumber("123456789")
                .departments(departmentsDTOs())
                .build();
    }

    static UpdateEmployeeDTO updateEmployeeDTO() {
        return UpdateEmployeeDTO.builder()
                .employeeId(employeeId)
                .phoneNumber("123456789")
                .departments(departmentsDTOs())
                .build();
    }

    static UpdateEmployeeDTO updateEmployeeDTONotValid() {
        return UpdateEmployeeDTO.builder()
                .employeeId(employeeId)
                .phoneNumber("123456789andThenMoreChar")
                .departments(departmentsDTOs())
                .build();
    }

    static Qualification qualification() {
        return Qualification.builder().description("description").build();
    }

    static Machine machine() {
        return Machine.builder()
                .machineType(MachineType.builder().name("ImplementThis").build())
                .make("make")
                .model("model")
                .year(2020)
                .broken(false)
                .build();
    }

    static Set<Department> departments() {
        return Set.of(Department.builder().name("departments").build());
    }

    static Set<DepartmentDTO> departmentsDTOs() {
        return Set.of(DepartmentDTO.builder().name("department").build());
    }

    private static Set<Qualification> getQualifications() {
        return Set.of(
                Qualification.builder().description("foo").build(),
                Qualification.builder().description("bar").build());
    }

    private static Set<Machine> getMachines() {
        return Set.of(
                machine(),
                Machine.builder()
                        .machineType(MachineType.builder().name("ImplementThis").build())
                        .make("make")
                        .model("model")
                        .year(2020)
                        .broken(false)
                        .build());
    }
}
