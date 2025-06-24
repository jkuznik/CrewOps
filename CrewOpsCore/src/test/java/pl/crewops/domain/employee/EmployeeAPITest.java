package pl.crewops.domain.employee;

import static org.assertj.core.api.Assertions.assertThat;
import static pl.crewops.domain.employee.EmployeeTestFactory.*;

import jakarta.validation.ConstraintViolationException;
import java.util.UUID;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import pl.crewops.IntegrationTest;
import pl.crewops.dto.employee.EmployeeDTO;
import pl.crewops.dto.employee.UpdateEmployeeDTO;
import pl.crewops.infrastructure.multitenancy.TenantContext;
import pl.crewops.model.Employee;

@Transactional
class EmployeeAPITest extends IntegrationTest {

    @Autowired
    private EmployeeAPI employeeAPI;

    @Autowired
    private EmployeeRepository employeeRepository;

    @Test
    void shouldReturnEmployeeWithNoQualificationsAndNoVehicles() {
        // given
        TenantContext.setCurrentTenant(TEST_TENANT);
        var createEmployeeDTO = EmployeeTestFactory.createEmployeeDTO();

        // when
        EmployeeDTO result = employeeAPI.createEmployee(createEmployeeDTO);
        Employee employee = employeeRepository
                .findByFirstNameAndLastName("firstName", "lastName")
                .getFirst();

        // then
        assertThat(result.firstName()).isEqualTo("firstName");
        assertThat(result.firstName()).isEqualTo(employee.getFirstName());
    }

    @Test
    void shouldThrowException_whenCreateEmployeeDTOIsNotValid() {
        // given
        var createEmployeeDTOWithNullFields = createEmployeeDTONotValid();
        // when
        Exception result = Assertions.catchException(() -> employeeAPI.createEmployee(createEmployeeDTOWithNullFields));

        // then
        assertThat(result).isNotNull();
        assertThat(result).isExactlyInstanceOf(ConstraintViolationException.class);
    }

    @Test
    void shouldThrowException_whenUpdateEmployeeDTOIsNotValid() {
        // given
        var updateEmployeeDTONotValid = updateEmployeeDTONotValid();
        Exception result = Assertions.catchException(() -> employeeAPI.updateEmployee(updateEmployeeDTONotValid));

        // then
        assertThat(result).isNotNull();
        assertThat(result).isExactlyInstanceOf(ConstraintViolationException.class);
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
        assertThat(result.firstName()).isEqualTo("Jan");
        assertThat(result.lastName()).isEqualTo("Kowalski");
        assertThat(result.phoneNumber()).isEqualTo("987654321");
        assertThat(result.department()).isEqualTo("foo");
    }
}
