package pl.crewops.domain.employee;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchException;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static pl.crewops.domain.employee.EmployeeTestFactory.*;

import jakarta.validation.ConstraintViolationException;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
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
import pl.crewops.exception.ExpireAtException;
import pl.crewops.model.Employee;
import pl.crewops.model.Qualification;
import pl.crewops.model.Vehicle;
import pl.crewops.model.joinTable.EmployeeQualification;

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
    private Qualification qualification;
    private Vehicle vehicle;
    private UUID employeeId = UUID.randomUUID();
    private UUID qualificationId = UUID.randomUUID();
    private UUID vehicleId = UUID.randomUUID();

    @BeforeEach
    void setUp() {
        createEmployeeWithQAndV = createEmployeeDTOWithQualificationsAndVehicles();
        createEmployeeWithEmptyQAndEmptyV = createEmployeeDTOWithoutQualificationsAndVehicles();
        createEmployeeDTOWithNullFields = createEmployeeDTONotValid();
        updateEmployeeDTO = updateEmployeeDTO();
        updateEmployeeDTONotValid = updateEmployeeDTONotValid();
        employeeWithQAndV = createEmployeeWithQualificationsAndVehicles();
        employeeWithEmptyQAndEmptyV = createEmployeeWithoutQualificationsAndVehicles();
        qualification = qualification();
        vehicle = vehicle();
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
        // when
        when(employeeRepository.findById(any(UUID.class))).thenReturn(Optional.of(employeeWithQAndV));
        EmployeeDTO result = employeeService.removePhoneNumber(qualificationId);

        // then
        assertThat(result).isNotNull();
        assertThat(result.phoneNumber()).isEqualTo(null);
    }

    @Test
    void shouldTriggerDeleteEntityMethod() {
        // when
        Mockito.doNothing().when(employeeRepository).deleteById(any(UUID.class));
        employeeService.deleteEmployee(employeeId);

        // then
        Mockito.verify(employeeRepository, Mockito.times(1)).deleteById(any(UUID.class));
    }

    @Test
    void shouldReturnEmployeeDTO_afterSuccessfulAddQualification() {
        // given
        qualification.setId(qualificationId);

        // when
        when(employeeRepository.findById(any(UUID.class))).thenReturn(Optional.of(employeeWithEmptyQAndEmptyV));
        when(qualificationAPI.getQualification(any(UUID.class))).thenReturn(qualification);
        EmployeeDTO result = employeeService.addQualification(employeeId, qualificationId);

        // then
        assertThat(result).isNotNull();
        assertThat(result.qualifications().size()).isEqualTo(1);
        assertThat(result.qualifications().contains(qualificationId)).isTrue();
    }

    @Test
    void shouldReturnEmployeeDTO_whenUpdateQualificationExpiredAtIsValid() {
        // given
        var expireAt = Instant.now().plusSeconds(3600);
        var eq = new EmployeeQualification();

        // when
        when(employeeQualificationRepository.findByEmployeeQualificationId(any(UUID.class), any(UUID.class)))
                .thenReturn(Optional.of(eq));
        when(employeeRepository.findById(any(UUID.class))).thenReturn(Optional.of(employeeWithQAndV));
        EmployeeDTO result = employeeService.updateQualificationExpiredAt(employeeId, qualificationId, expireAt);

        // then
        assertThat(result).isNotNull();
    }

    @Test
    void shouldThrowException_whenUpdateQualificationExpiredAtIsInThePast() {
        // given
        var expireAt = Instant.now().minusSeconds(3600);
        var eq = new EmployeeQualification();

        // when
        when(employeeQualificationRepository.findByEmployeeQualificationId(any(UUID.class), any(UUID.class)))
                .thenReturn(Optional.of(eq));
        Exception result = catchException(
                () -> employeeService.updateQualificationExpiredAt(employeeId, qualificationId, expireAt));

        // then
        assertThat(result).isExactlyInstanceOf(ExpireAtException.class);
    }

    @Test
    void shouldThrowException_whenUpdateQualificationExpiredAtIsNull() {
        // given
        var expireAt = Instant.now().minusSeconds(3600);
        var eq = new EmployeeQualification();

        // when
        when(employeeQualificationRepository.findByEmployeeQualificationId(any(UUID.class), any(UUID.class)))
                .thenReturn(Optional.of(eq));
        Exception result =
                catchException(() -> employeeService.updateQualificationExpiredAt(employeeId, qualificationId, null));

        // then
        assertThat(result).isExactlyInstanceOf(ExpireAtException.class);
    }

    @Test
    void shouldRemoveQualificationFromEmployee_whenEmployeeAndQualificationExist() {
        // given
        employeeWithEmptyQAndEmptyV.getQualifications().add(qualification);
        int before = employeeWithEmptyQAndEmptyV.getQualifications().size();

        // when
        when(employeeRepository.findById(any(UUID.class))).thenReturn(Optional.of(employeeWithEmptyQAndEmptyV));
        when(qualificationAPI.getQualification(any(UUID.class))).thenReturn(qualification);

        // then
        employeeService.removeQualification(employeeId, qualificationId);
        int after = employeeWithEmptyQAndEmptyV.getQualifications().size();
        assertThat(before).isEqualTo(1);
        assertThat(after).isEqualTo(0);
    }

    @Test
    void shouldReturnEmployeeDTO_afterSuccessfulAddVehicle() {
        // given
        vehicle.setId(vehicleId);

        // when
        when(employeeRepository.findById(any(UUID.class))).thenReturn(Optional.of(employeeWithEmptyQAndEmptyV));
        when(vehicleAPI.getVehicle(any(UUID.class))).thenReturn(vehicle);
        employeeService.addVehicle(employeeId, vehicleId);
        EmployeeDTO result = employeeService.addVehicle(employeeId, vehicleId);

        // then
        assertThat(result).isNotNull();
        assertThat(result.vehicles().size()).isEqualTo(1);
        assertThat(result.vehicles().contains(vehicle.getId())).isTrue();
    }

    @Test
    void shouldRemoveVehicleFromEmployee_whenEmployeeAndVehicleExist() {
        // given
        employeeWithEmptyQAndEmptyV.getVehicles().add(vehicle);
        int before = employeeWithEmptyQAndEmptyV.getVehicles().size();

        // when
        when(employeeRepository.findById(any(UUID.class))).thenReturn(Optional.of(employeeWithEmptyQAndEmptyV));
        when(vehicleAPI.getVehicle(vehicleId)).thenReturn(vehicle);

        // then
        employeeService.removeVehicle(employeeId, vehicleId);
        int after = employeeWithEmptyQAndEmptyV.getVehicles().size();
        assertThat(before).isEqualTo(1);
        assertThat(after).isEqualTo(0);
    }
}
