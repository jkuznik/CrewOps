package pl.kuznik.domain.employee;

import jakarta.validation.ConstraintViolationException;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import org.springframework.validation.beanvalidation.MethodValidationPostProcessor;
import pl.kuznik.domain.employee.dto.CreateEmployeeDTO;
import pl.kuznik.domain.employee.dto.EmployeeDTO;
import pl.kuznik.entity.Employee;

import static org.assertj.core.api.Assertions.assertThat;
import static pl.kuznik.domain.employee.EmployeeTestFactory.*;

@SpringJUnitConfig(classes = {EmployeeService.class, MethodValidationPostProcessor.class})
class EmployeeServiceTest {

    @MockitoBean
    EmployeeRepository employeeRepository;

    @Autowired
    EmployeeService employeeService;

    private CreateEmployeeDTO createEmployeeWithQAndV;
    private CreateEmployeeDTO createEmployeeWithEmptyQAndEmptyV;
    private CreateEmployeeDTO createEmployeeDTOWithNullFields;
    private Employee employeeWithQAndV;
    private Employee employeeWithEmptyQAndEmptyV;

    @BeforeEach
    void setUp() {
        createEmployeeWithQAndV = createEmployeeDTOWithQualificationsAndVehicles();
        createEmployeeWithEmptyQAndEmptyV = createEmployeeDTOWithoutQualificationsAndVehicles();
        createEmployeeDTOWithNullFields = createNotValidEmployeeDTO();
        employeeWithQAndV = createEmployeeWithQualificationsAndVehicles();
        employeeWithEmptyQAndEmptyV = createEmployeeWithoutQualificationsAndVehicles();
    }

    @Test
    void shouldReturnEmployeeDTO_whenCreateEmployeeDTOHaveNoQualificationsAndNoVehicles() {
        // when
        Mockito.when(employeeRepository.save(Mockito.any(Employee.class))).thenReturn(employeeWithEmptyQAndEmptyV);
        EmployeeDTO result = employeeService.createEmployee(createEmployeeWithEmptyQAndEmptyV);

        // then
        assertThat(result).isNotNull();
        assertThat(result.firstName()).isEqualTo("firstName");
    }

    @Test
    void shouldReturnEmployeeDTO_whenCreateEmployeeDTOHaveQualificationsAndNoVehicles() {
        // when
        Mockito.when(employeeRepository.save(Mockito.any(Employee.class))).thenReturn(employeeWithQAndV);
        EmployeeDTO result = employeeService.createEmployee(createEmployeeWithQAndV);

        // then
        assertThat(result).isNotNull();
        assertThat(result.firstName()).isEqualTo("firstName");
        assertThat(result.qualifications().size()).isEqualTo(2);
    }

    @Test
    void shouldThrowException_whenCreateEmployeeDTOIsNotValid() {
        // when
        Exception result = Assertions.catchException(() -> employeeService.createEmployee(createEmployeeDTOWithNullFields));

        // then
        assertThat(result).isNotNull();
        assertThat(result).isExactlyInstanceOf(ConstraintViolationException.class);
    }

    @Test
    void updateEmployee() {}
}
