package pl.crewops.domain.employee;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchException;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static pl.crewops.domain.employee.EmployeeTestFactory.*;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import pl.crewops.domain.auth.AuthAPI;
import pl.crewops.domain.machine.MachineAPI;
import pl.crewops.domain.qualification.QualificationAPI;
import pl.crewops.dto.employee.CreateEmployeeDTO;
import pl.crewops.dto.employee.EmployeeDTO;
import pl.crewops.dto.employee.UpdateEmployeeDTO;
import pl.crewops.dto.qualification.UpdateQualificationExpiredAtDTO;
import pl.crewops.exception.domain.employee.ExpireAtException;
import pl.crewops.model.Employee;
import pl.crewops.model.Machine;
import pl.crewops.model.Qualification;
import pl.crewops.model.joinTable.EmployeeQualification;
import pl.crewops.model.publicSchema.AuthUser;

@SpringJUnitConfig(
        classes = {
            EmployeeService.class,
            EmployeeRepository.class,
            EmployeeQualificationRepository.class,
            QualificationAPI.class,
            MachineAPI.class,
            AuthAPI.class
        })
class EmployeeServiceTest {

    @Autowired
    EmployeeService employeeService;

    @MockitoBean
    EmployeeRepository employeeRepository;

    @MockitoBean
    EmployeeQualificationRepository employeeQualificationRepository;

    @MockitoBean
    QualificationAPI qualificationAPI;

    @MockitoBean
    MachineAPI machineAPI;

    @MockitoBean
    AuthAPI authAPI;

    private CreateEmployeeDTO createEmployeeWithEmptyQAndEmptyV;
    private UpdateEmployeeDTO updateEmployeeDTO;
    private Employee employeeWithQAndV;
    private Employee employeeWithEmptyQAndEmptyV;
    private Qualification qualification;
    private Machine machine;
    private final UUID employeeId = UUID.randomUUID();
    private final UUID qualificationId = UUID.randomUUID();
    private final UUID machineId = UUID.randomUUID();

    @BeforeEach
    void setUp() {
        createEmployeeWithEmptyQAndEmptyV = EmployeeTestFactory.createEmployeeDTO();
        updateEmployeeDTO = updateEmployeeDTO();
        employeeWithQAndV = employeeWithQualificationsAndMachines();
        employeeWithEmptyQAndEmptyV = employeeWithoutQualificationsAndMachines();
        qualification = qualification();
        machine = machine();
    }

    @Test
    void createEmployee_ShouldReturnEmployeeDTO_whenCreateEmployeeDTOHaveNoQualificationsAndNoMachines() {
        // when
        when(employeeRepository.save(any(Employee.class))).thenReturn(employeeWithEmptyQAndEmptyV);
        EmployeeDTO result = employeeService.createEmployee(createEmployeeWithEmptyQAndEmptyV);

        // then
        assertThat(result).isNotNull();
        assertThat(result.firstName()).isEqualTo("firstName");
    }

    @Test
    void shouldReturnEmployeeDTO_whenUpdateEmployeeDTOHaveNoQualificationsAndNoMachines() {
        // when
        when(employeeRepository.findById(any(UUID.class))).thenReturn(Optional.of(employeeWithEmptyQAndEmptyV));
        EmployeeDTO result = employeeService.updateEmployee(updateEmployeeDTO);

        // then
        assertThat(result).isNotNull();
        assertThat(result.firstName()).isEqualTo("firstName");
    }

    @Test
    void shouldReturnEmployeeDTOsList_whenEmployeesExist() {
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
    void shouldReturnEmployeeDTOWithRequiredMachines_whenEmployeesExist() {
        // given
        Page<Employee> employees = new PageImpl<>(List.of(employeeWithQAndV));

        // when
        when(employeeRepository.findByMachinesId(any(UUID.class), any(Pageable.class)))
                .thenReturn(employees);
        List<EmployeeDTO> result = employeeService.getEmployeesByMachines(qualificationId, 0, 5);

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
        // given
        var authUser = AuthUser.builder().username("username").build();

        // when
        when(employeeRepository.findById(any(UUID.class))).thenReturn(Optional.of(employeeWithQAndV));

        employeeService.deleteEmployee(employeeId);

        // then
        assertThat(employeeWithQAndV.isActive()).isFalse();
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
        assertThat(result.qualifications().stream().findFirst().get().description())
                .isEqualTo(qualification.getDescription());
    }

    @Test
    void shouldReturnEmployeeDTO_whenUpdateQualificationExpiredAtIsValid() {
        // given
        var updateQualificationExpireAt = UpdateQualificationExpiredAtDTO.builder()
                .employeeId(employeeId)
                .qualificationId(qualificationId)
                .expiredAt(Instant.now().plusSeconds(3600))
                .build();
        var eq = new EmployeeQualification();

        // when
        when(employeeQualificationRepository.findByEmployeeQualificationId(any(UUID.class), any(UUID.class)))
                .thenReturn(Optional.of(eq));
        when(employeeRepository.findById(any(UUID.class))).thenReturn(Optional.of(employeeWithQAndV));
        EmployeeDTO result =
                employeeService.updateQualificationExpiredAt(employeeId, qualificationId, updateQualificationExpireAt);

        // then
        assertThat(result).isNotNull();
    }

    @Test
    void shouldThrowException_whenUpdateQualificationExpiredAtIsInThePast() {
        // given
        var updateQualificationExpireAt = UpdateQualificationExpiredAtDTO.builder()
                .employeeId(employeeId)
                .qualificationId(qualificationId)
                .expiredAt(Instant.now().minusSeconds(3600))
                .build();
        var eq = new EmployeeQualification();

        // when
        when(employeeQualificationRepository.findByEmployeeQualificationId(any(UUID.class), any(UUID.class)))
                .thenReturn(Optional.of(eq));
        Exception result = catchException(() ->
                employeeService.updateQualificationExpiredAt(employeeId, qualificationId, updateQualificationExpireAt));

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
    void shouldReturnEmployeeDTO_afterSuccessfulAddMachine() {
        // given
        machine.setId(machineId);

        // when
        when(employeeRepository.findById(any(UUID.class))).thenReturn(Optional.of(employeeWithEmptyQAndEmptyV));
        when(machineAPI.getMachine(any(UUID.class))).thenReturn(machine);
        employeeService.addMachine(employeeId, machineId);
        EmployeeDTO result = employeeService.addMachine(employeeId, machineId);

        // then
        assertThat(result).isNotNull();
        assertThat(result.machines().size()).isEqualTo(1);
        assertThat(result.machines().stream().findFirst().get().id()).isEqualTo(machineId);
    }

    @Test
    void shouldRemoveMachineFromEmployee_whenEmployeeAndMachineExist() {
        // given
        employeeWithEmptyQAndEmptyV.getMachines().add(machine);
        int before = employeeWithEmptyQAndEmptyV.getMachines().size();

        // when
        when(employeeRepository.findById(any(UUID.class))).thenReturn(Optional.of(employeeWithEmptyQAndEmptyV));
        when(machineAPI.getMachine(machineId)).thenReturn(machine);

        // then
        employeeService.removeMachine(employeeId, machineId);
        int after = employeeWithEmptyQAndEmptyV.getMachines().size();
        assertThat(before).isEqualTo(1);
        assertThat(after).isEqualTo(0);
    }
}
