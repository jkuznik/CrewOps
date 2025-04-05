package pl.crewops.domain.employee;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static pl.crewops.domain.employee.EmployeeTestFactory.*;

import jakarta.validation.ConstraintViolationException;
import java.util.Optional;
import java.util.UUID;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import org.springframework.validation.beanvalidation.MethodValidationPostProcessor;
import pl.crewops.domain.qualification.QualificationAPI;
import pl.crewops.dto.employee.CreateEmployeeDTO;
import pl.crewops.dto.employee.EmployeeDTO;
import pl.crewops.dto.employee.UpdateEmployeeDTO;
import pl.crewops.model.Employee;

@SpringJUnitConfig(classes = {EmployeeService.class, MethodValidationPostProcessor.class})
class EmployeeServiceTest {

    @MockitoBean
    EmployeeRepository employeeRepository;

    @MockitoBean
    EmployeeQualificationRepository employeeQualificationRepository;

    @MockitoBean
    QualificationAPI qualificationAPI;

    @Autowired
    EmployeeService employeeService;

    private CreateEmployeeDTO createEmployeeWithQAndV;
    private CreateEmployeeDTO createEmployeeWithEmptyQAndEmptyV;
    private CreateEmployeeDTO createEmployeeDTOWithNullFields;
    private UpdateEmployeeDTO updateEmployeeDTO;
    private UpdateEmployeeDTO updateEmployeeDTONotValid;
    private Employee employeeWithQAndV;
    private Employee employeeWithEmptyQAndEmptyV;

    @BeforeEach
    void setUp() {
        createEmployeeWithQAndV = createEmployeeDTOWithQualificationsAndVehicles();
        createEmployeeWithEmptyQAndEmptyV = createEmployeeDTOWithoutQualificationsAndVehicles();
        createEmployeeDTOWithNullFields = createEmployeeDTONotValid();
        updateEmployeeDTO = updateEmployeeDTO();
        updateEmployeeDTONotValid = updateEmployeeDTONotValid();
        employeeWithQAndV = createEmployeeWithQualificationsAndVehicles();
        employeeWithEmptyQAndEmptyV = createEmployeeWithoutQualificationsAndVehicles();
    }

    @Test
    void shouldReturnEmployeeDTO_whenCreateEmployeeDTOHaveNoQualificationsAndNoVehicles() {
        // when
        Mockito.when(employeeRepository.save(any(Employee.class))).thenReturn(employeeWithEmptyQAndEmptyV);
        EmployeeDTO result = employeeService.createEmployee(createEmployeeWithEmptyQAndEmptyV);

        // then
        assertThat(result).isNotNull();
        assertThat(result.firstName()).isEqualTo("firstName");
    }

    @Test
    void shouldReturnEmployeeDTO_whenCreateEmployeeDTOHaveQualificationsAndNoVehicles() {
        // when
        Mockito.when(employeeRepository.save(any(Employee.class))).thenReturn(employeeWithQAndV);
        EmployeeDTO result = employeeService.createEmployee(createEmployeeWithQAndV);

        // then
        assertThat(result).isNotNull();
        assertThat(result.firstName()).isEqualTo("firstName");
        assertThat(result.qualifications().size()).isEqualTo(2);
    }

    @Test
    void shouldThrowException_whenCreateEmployeeDTOIsNotValid() {
        // when
        Exception result =
                Assertions.catchException(() -> employeeService.createEmployee(createEmployeeDTOWithNullFields));

        // then
        assertThat(result).isNotNull();
        assertThat(result).isExactlyInstanceOf(ConstraintViolationException.class);
    }

    @Test
    void shouldReturnEmployeeDTO_whenUpdateEmployeeDTOHaveNoQualificationsAndNoVehicles() {
        // when
        Mockito.when(employeeRepository.findById(any(UUID.class))).thenReturn(Optional.of(employeeWithEmptyQAndEmptyV));
        EmployeeDTO result = employeeService.updateEmployee(updateEmployeeDTO);

        // then
        assertThat(result).isNotNull();
        assertThat(result.firstName()).isEqualTo("firstName");
    }

    @Test
    void shouldThrowException_whenUpdateEmployeeDTOIsNotValid() {
        // given
        Exception result = Assertions.catchException(() -> employeeService.updateEmployee(updateEmployeeDTONotValid));

        // then
        assertThat(result).isNotNull();
        assertThat(result).isExactlyInstanceOf(ConstraintViolationException.class);
    }
}
