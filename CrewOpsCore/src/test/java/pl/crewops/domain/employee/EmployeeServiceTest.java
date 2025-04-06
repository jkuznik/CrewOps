package pl.crewops.domain.employee;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static pl.crewops.domain.employee.EmployeeTestFactory.*;

import jakarta.validation.ConstraintViolationException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import org.springframework.validation.beanvalidation.MethodValidationPostProcessor;
import pl.crewops.domain.qualification.QualificationAPI;
import pl.crewops.domain.vehicle.VehicleAPI;
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

    @MockitoBean
    VehicleAPI vehicleAPI;

    @Autowired
    EmployeeService employeeService;

    private CreateEmployeeDTO createEmployeeWithQAndV;
    private CreateEmployeeDTO createEmployeeWithEmptyQAndEmptyV;
    private CreateEmployeeDTO createEmployeeDTOWithNullFields;
    private UpdateEmployeeDTO updateEmployeeDTO;
    private UpdateEmployeeDTO updateEmployeeDTONotValid;
    private Employee employeeWithQAndV;
    private Employee employeeWithEmptyQAndEmptyV;
    private EmployeeDTO employeeDTO;

    @BeforeEach
    void setUp() {
        createEmployeeWithQAndV = createEmployeeDTOWithQualificationsAndVehicles();
        createEmployeeWithEmptyQAndEmptyV = createEmployeeDTOWithoutQualificationsAndVehicles();
        createEmployeeDTOWithNullFields = createEmployeeDTONotValid();
        updateEmployeeDTO = updateEmployeeDTO();
        updateEmployeeDTONotValid = updateEmployeeDTONotValid();
        employeeWithQAndV = createEmployeeWithQualificationsAndVehicles();
        employeeWithEmptyQAndEmptyV = createEmployeeWithoutQualificationsAndVehicles();
        employeeDTO = createEmployeeDTO();
    }

    @Test
    void shouldReturnEmployeeDTO_whenCreateEmployeeDTOHaveNoQualificationsAndNoVehicles() {
        // when
        when(employeeRepository.save(any(Employee.class))).thenReturn(employeeWithEmptyQAndEmptyV);
        EmployeeDTO result = employeeService.createEmployee(createEmployeeWithEmptyQAndEmptyV);

        // then
        assertThat(result).isNotNull();
        assertThat(result.firstName()).isEqualTo("firstName");
    }

    @Test
    void shouldReturnEmployeeDTO_whenCreateEmployeeDTOHaveQualificationsAndNoVehicles() {
        // when
        when(employeeRepository.save(any(Employee.class))).thenReturn(employeeWithQAndV);
        EmployeeDTO result = employeeService.createEmployee(createEmployeeWithQAndV);

        // then
        assertThat(result).isNotNull();
        assertThat(result.firstName()).isEqualTo("firstName");
        //        assertThat(result.qualifications().size()).isEqualTo(2);
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
        when(employeeRepository.findById(any(UUID.class))).thenReturn(Optional.of(employeeWithEmptyQAndEmptyV));
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

    @Test
    void shouldReturnEmployeeDTOList_whenEmployeesExist() {
        // given
        Page<Employee> employees = new PageImpl<>(List.of(employeeWithQAndV, employeeWithEmptyQAndEmptyV));

        // when
        when(employeeRepository.findAll(any(Pageable.class))).thenReturn(employees);
        List<EmployeeDTO> result = employeeService.getAllEmployees(0, 5);

        // then
        assertThat(result).isNotNull();
        assertThat(result.size()).isEqualTo(2);
        assertThat(result.get(0).firstName()).isEqualTo("firstName");
    }

    @Test
    void shouldReturnEmployeeDTOWithRequiredQualifications_whenEmployeesExist() {
        // given
        Page<Employee> employees = new PageImpl<>(List.of(employeeWithQAndV));
        var qualificationId = UUID.randomUUID();
        // when
        when(employeeRepository.findByQualificationId(any(UUID.class), any(Pageable.class)))
                .thenReturn(employees);
        List<EmployeeDTO> result = employeeService.getEmployeesByQualification(qualificationId, 0, 5);

        // then
        assertThat(result).isNotNull();
        assertThat(result.size()).isEqualTo(1);
        assertThat(result.get(0).firstName()).isEqualTo("firstName");
    }

    @Test
    void shouldReturnEmployeeDTOWithRequiredVehicles_whenEmployeesExist() {
        // given
        Page<Employee> employees = new PageImpl<>(List.of(employeeWithQAndV));
        var qualificationId = UUID.randomUUID();
        // when
        when(employeeRepository.findByVehiclesId(any(UUID.class), any(Pageable.class)))
                .thenReturn(employees);
        List<EmployeeDTO> result = employeeService.getEmployeesByVehicles(qualificationId, 0, 5);

        // then
        assertThat(result).isNotNull();
        assertThat(result.size()).isEqualTo(1);
        assertThat(result.get(0).firstName()).isEqualTo("firstName");
    }

    @Test
    void shouldRemovePhoneNumber_whenEmployeeHasPhoneNumber() {
        // given
        var qualificationId = UUID.randomUUID();

        // when
        when(employeeRepository.findById(any(UUID.class))).thenReturn(Optional.of(employeeWithQAndV));
        EmployeeDTO result = employeeService.removePhoneNumber(qualificationId);

        // then
        assertThat(result).isNotNull();
        assertThat(result.phoneNumber()).isEqualTo(null);
    }

    @Test
    void deleteEmployee() {}
}
