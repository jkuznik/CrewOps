package pl.kuznik.domain.employee;

import static org.assertj.core.api.Assertions.assertThat;
import static pl.kuznik.domain.employee.EmployeeTestFactory.*;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import pl.kuznik.IntegrationTest;
import pl.kuznik.domain.employee.dto.EmployeeDTO;
import pl.kuznik.domain.employee.dto.UpdateEmployeeDTO;
import pl.kuznik.entity.Employee;
import pl.kuznik.entity.Qualification;
import pl.kuznik.entity.Vehicle;
import pl.kuznik.utils.enums.VehicleType;

@Transactional
class EmployeeAPITest extends IntegrationTest {

    @Autowired
    private EmployeeAPI employeeAPI;

    @Autowired
    private EmployeeRepository employeeRepository;

    @Test
    void shouldReturnEmployeeWithNoQualificationsAndNoVehicles() {
        // given
        var employeeDTO = createEmployeeDTOWithoutQualificationsAndVehicles();

        // when
        EmployeeDTO result = employeeAPI.createEmployee(employeeDTO);
        Employee employee = employeeRepository.findAll().getFirst();

        // then
        assertThat(result.firstName()).isEqualTo("firstName");
        assertThat(result.firstName()).isEqualTo(employee.getFirstName());
    }

    @Test
    void shouldReturnEmployeeWithQualificationsButNoVehicles() {
        // given
        var employeeDTO = createEmployeeDTOWithQualificationsAndVehicles();

        // when
        EmployeeDTO result = employeeAPI.createEmployee(employeeDTO);
        Employee employee = employeeRepository.findAll().getFirst();
        Set<Qualification> employeeQualifications = employee.getQualifications();

        // then
        assertThat(result.firstName()).isEqualTo("firstName");
        assertThat(result.firstName()).isEqualTo(employee.getFirstName());
        assertThat(employeeQualifications.size()).isEqualTo(2); // check if qualifications are persist correct
    }

    @Test
    void shouldReturnEmployeeWithQualificationsAndVehicles() {
        // given
        var employeeDTO = createEmployeeDTOWithQualificationsAndVehicles();

        // when
        EmployeeDTO result = employeeAPI.createEmployee(employeeDTO);
        Employee employee = employeeRepository.findAll().getFirst();
        Set<Qualification> employeeQualifications = employee.getQualifications();
        Set<Vehicle> employeeVehicles = employee.getVehicles();
        Optional<Vehicle> first = employeeVehicles.stream()
                .filter(vehicle -> vehicle.getVehicleType().equals(VehicleType.BULLDOZER))
                .findFirst();

        // then
        assertThat(result.firstName()).isEqualTo("firstName");
        assertThat(result.firstName()).isEqualTo(employee.getFirstName());
        assertThat(employeeQualifications.size()).isEqualTo(2); // check if qualifications are persist correct
        assertThat(employeeVehicles.size()).isEqualTo(2); // check if vehicles are persist correct
        assertThat(first.isPresent()).isTrue(); // check if vehicle type is persist correct
    }

    @Test
    void shouldReturnEmployeeWhenUpdateObjectIsValid() {

         // given
                var updateEmployeeDTO = UpdateEmployeeDTO.builder()
                        .employeeId(UUID.fromString("11111111-1111-1111-1111-111111111111"))
                        .phoneNumber("987654321")
                        .department("foo")
                        .build();

                // when
                EmployeeDTO result = employeeAPI.updateEmployee(updateEmployeeDTO);

                // then
                assertThat(result.firstName()).isEqualTo("John");
                assertThat(result.lastName()).isEqualTo("Doe");
                assertThat(result.phoneNumber()).isEqualTo("987654321");
                assertThat(result.department()).isEqualTo("foo");
    }
}
