package pl.crewops.domain.employee;

import static org.assertj.core.api.Assertions.assertThat;
import static pl.crewops.domain.employee.EmployeeTestFactory.*;

import jakarta.validation.ConstraintViolationException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.UUID;
import javax.sql.DataSource;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import pl.crewops.IntegrationTest;
import pl.crewops.model.Employee;
import pl.crewops.model.dto.department.DepartmentDTO;
import pl.crewops.model.dto.employee.EmployeeDTO;
import pl.crewops.model.dto.employee.UpdateEmployeeDTO;

@Transactional
class EmployeeAPITest extends IntegrationTest {

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private DataSource dataSource;

    @Test
    void printCurrentSchema() throws SQLException {
        try (Connection conn = dataSource.getConnection();
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery("SELECT current_schema()")) {
            if (rs.next()) {
                System.out.println("Current schema: " + rs.getString(1));
            }
        }
    }

    @Test
    void shouldReturnEmployeeWithNoQualificationsAndNoVehicles() {
        // given
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
                .departments(departmentsDTOs())
                .build();

        // when
        EmployeeDTO result = employeeAPI.updateEmployee(updateEmployeeDTO);

        // then
        assertThat(result.firstName()).isEqualTo("Jan");
        assertThat(result.lastName()).isEqualTo("Kowalski");
        assertThat(result.phoneNumber()).isEqualTo("987654321");
        assertThat(result.departments()
                        .contains(DepartmentDTO.builder().name("BHP").build()))
                .isTrue();
    }

    @Test
    void getEmployeeById_nPlus1() {
        // given
        Employee employeeById = employeeAPI.getEmployeeById(UUID.fromString("11111111-1111-1111-1111-111111111111"));

        assertThat(employeeById.getQualifications().isEmpty()).isFalse();
        assertThat(employeeById.getMachines().isEmpty()).isFalse();
        assertThat(employeeById.getMachines().stream().findFirst().get().getMachineType())
                .isNotNull();
    }
}
