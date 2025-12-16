package pl.crewops.domain.employee;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchException;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static pl.crewops.domain.employee.EmployeeTestFactory.*;

import java.time.Instant;
import java.time.ZoneId;
import java.util.*;
import java.util.stream.Collectors;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import pl.crewops.domain.auth.AuthAPI;
import pl.crewops.domain.department.DepartmentAPI;
import pl.crewops.domain.machine.MachineAPI;
import pl.crewops.domain.qualification.QualificationAPI;
import pl.crewops.exception.domain.employee.ExpireAtException;
import pl.crewops.model.compositePK.EmployeeQualificationId;
import pl.crewops.model.dto.auth.RoleDTO;
import pl.crewops.model.dto.department.DepartmentDTO;
import pl.crewops.model.dto.employee.CreateEmployeeDTO;
import pl.crewops.model.dto.employee.EmployeeDTO;
import pl.crewops.model.dto.employee.EmployeeQualificationDTO;
import pl.crewops.model.dto.employee.UpdateEmployeeDTO;
import pl.crewops.model.dto.machine.MachineDTO;
import pl.crewops.model.dto.qualification.QualificationDTO;
import pl.crewops.model.dto.qualification.UpdateQualificationExpiredAtDTO;
import pl.crewops.model.joinTable.EmployeeQualification;
import pl.crewops.model.tenantSchema.Employee;
import pl.crewops.model.tenantSchema.Machine;
import pl.crewops.model.tenantSchema.Qualification;
import pl.crewops.util.spring.SpringContextBridge;

@SpringJUnitConfig(classes = {EmployeeService.class})
class EmployeeServiceTest {

    @Autowired
    ApplicationContext applicationContext;

    @Autowired
    EmployeeService employeeService;

    @MockitoBean
    EmployeeRepository employeeRepository;

    @MockitoBean
    EmployeeQualificationRepository employeeQualificationRepository;

    @MockitoBean
    QualificationAPI qualificationAPI;

    @MockitoBean
    DepartmentAPI departmentAPI;

    @MockitoBean
    MachineAPI machineAPI;

    @MockitoBean
    AuthAPI authAPI;

    @MockitoBean
    EmployeeMapper employeeMapper; // <- mockujemy mapper

    private CreateEmployeeDTO createEmployeeDTO;
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
        createEmployeeDTO = createEmployeeDTO();
        updateEmployeeDTO = updateEmployeeDTO();
        employeeWithQAndV = employeeWithQualificationsAndMachines();
        employeeWithEmptyQAndEmptyV = employeeWithoutQualificationsAndMachines();
        qualification = qualification();
        machine = machine();
        SpringContextBridge.setApplicationContext(applicationContext);

        when(employeeMapper.toDTO(any(Employee.class))).thenAnswer(invocation -> {
            Employee e = invocation.getArgument(0);

            // mapowanie kolekcji na odpowiednie DTO
            Set<DepartmentDTO> departmentDTOs = e.getDepartments().stream()
                    .map(d -> DepartmentDTO.builder()
                            .id(d.getId())
                            .name(d.getName())
                            .build())
                    .collect(Collectors.toSet());

            Set<QualificationDTO> qualificationDTOs = e.getQualifications().stream()
                    .map(q -> QualificationDTO.builder()
                            .id(q.getId())
                            .description(q.getDescription())
                            .build())
                    .collect(Collectors.toSet());

            Set<MachineDTO> machineDTOs = e.getMachines().stream()
                    .map(m ->
                            MachineDTO.builder().id(m.getId()).make(m.getMake()).build())
                    .collect(Collectors.toSet());

            // puste role, bo w testach nie mapujemy ich z AuthAPI
            Set<RoleDTO> roleDTOs = new HashSet<>();

            return EmployeeDTO.builder()
                    .id(e.getId())
                    .firstName(e.getFirstName())
                    .lastName(e.getLastName())
                    .phoneNumber(e.getPhoneNumber())
                    .email(e.getEmail())
                    .active(e.isActive())
                    .departments(departmentDTOs)
                    .qualifications(qualificationDTOs)
                    .machines(machineDTOs)
                    .roles(roleDTOs)
                    .build();
        });

        when(employeeMapper.toDTO(any(EmployeeQualification.class))).thenAnswer(invocation -> {
            EmployeeQualification eq = invocation.getArgument(0);
            return EmployeeQualificationDTO.builder()
                    .employeeId(eq.getEmployee() != null ? eq.getEmployee().getId() : null)
                    .qualificationId(
                            eq.getQualification() != null
                                    ? eq.getQualification().getId()
                                    : null)
                    .expiredAt(
                            eq.getExpiredAt() != null
                                    ? eq.getExpiredAt()
                                            .atZone(ZoneId.systemDefault())
                                            .toLocalDate()
                                    : null)
                    .build();
        });
    }

    @Test
    void createEmployee_ShouldReturnEmployeeDTO_whenNoQualificationsAndMachines() {
        when(employeeMapper.toEntity(any(CreateEmployeeDTO.class))).thenReturn(employeeWithEmptyQAndEmptyV);
        when(employeeRepository.save(any(Employee.class))).thenReturn(employeeWithEmptyQAndEmptyV);
        when(employeeMapper.toDTO(any(Employee.class))).thenAnswer(invocation -> {
            Employee e = invocation.getArgument(0);
            return EmployeeDTO.builder()
                    .id(e.getId() != null ? e.getId() : UUID.randomUUID()) // jeśli brak ID, generuj tymczasowe
                    .firstName(e.getFirstName())
                    .lastName(e.getLastName())
                    .phoneNumber(e.getPhoneNumber())
                    .email(e.getEmail())
                    .active(e.isActive())
                    .departments(Set.of())
                    .roles(Set.of())
                    .qualifications(Set.of())
                    .machines(Set.of())
                    .build();
        });

        EmployeeDTO result = employeeService.createEmployee(createEmployeeDTO);

        assertThat(result).isNotNull();
        assertThat(result.firstName()).isEqualTo(employeeWithEmptyQAndEmptyV.getFirstName());
        verify(employeeRepository).save(any(Employee.class));
        verify(employeeMapper).toDTO(employeeWithEmptyQAndEmptyV);
    }

    @Test
    void updateEmployee_ShouldReturnEmployeeDTO_whenNoQualificationsAndMachines() {
        when(employeeRepository.findById(any())).thenReturn(Optional.of(employeeWithEmptyQAndEmptyV));

        EmployeeDTO result = employeeService.updateEmployee(updateEmployeeDTO);

        assertThat(result).isNotNull();
        assertThat(result.firstName()).isEqualTo(employeeWithEmptyQAndEmptyV.getFirstName());
        verify(employeeRepository).findById(any());
        verify(employeeMapper).toDTO(employeeWithEmptyQAndEmptyV);
    }

    @Test
    void getAllEmployees_ShouldReturnEmployeeDTOs() {
        Page<Employee> employees = new PageImpl<>(List.of(employeeWithQAndV, employeeWithEmptyQAndEmptyV));
        when(employeeRepository.findAll(any(Pageable.class))).thenReturn(employees);

        List<EmployeeDTO> result = employeeService.getAllEmployees(0, 5);

        assertThat(result).hasSize(2);
        verify(employeeRepository).findAll(any(Pageable.class));
        verify(employeeMapper, times(2)).toDTO(any(Employee.class));
    }

    @Test
    void addQualification_ShouldReturnEmployeeDTO() {
        qualification.setId(qualificationId);
        when(employeeRepository.findById(any(UUID.class))).thenReturn(Optional.of(employeeWithEmptyQAndEmptyV));
        when(qualificationAPI.getQualification(any(UUID.class))).thenReturn(qualification);

        EmployeeDTO result = employeeService.addQualification(employeeId, qualificationId);

        assertThat(result).isNotNull();
        assertThat(result.qualifications()).isNotEmpty();
        verify(employeeRepository).findById(employeeId);
        verify(qualificationAPI).getQualification(qualificationId);
    }

    @Test
    void updateQualificationExpiredAt_ShouldReturnDTO_whenValid() {
        var dto = UpdateQualificationExpiredAtDTO.builder()
                .employeeId(employeeId)
                .qualificationId(qualificationId)
                .expiredAt(Instant.now().plusSeconds(3600))
                .build();
        var eq = new EmployeeQualification();
        eq.setEmployee(employeeWithQAndV);
        eq.setQualification(qualification);

        when(employeeQualificationRepository.findByEmployeeQualificationId(any(UUID.class), any(UUID.class)))
                .thenReturn(Optional.of(eq));
        when(employeeRepository.findById(any(UUID.class))).thenReturn(Optional.of(employeeWithQAndV));

        EmployeeDTO result = employeeService.updateQualificationExpiredAt(employeeId, qualificationId, dto);

        assertThat(result).isNotNull();
    }

    @Test
    void updateQualificationExpiredAt_ShouldThrowException_whenInThePast() {
        var dto = UpdateQualificationExpiredAtDTO.builder()
                .employeeId(employeeId)
                .qualificationId(qualificationId)
                .expiredAt(Instant.now().minusSeconds(3600))
                .build();
        var eq = new EmployeeQualification();
        when(employeeQualificationRepository.findByEmployeeQualificationId(any(UUID.class), any(UUID.class)))
                .thenReturn(Optional.of(eq));

        Exception ex =
                catchException(() -> employeeService.updateQualificationExpiredAt(employeeId, qualificationId, dto));

        assertThat(ex).isExactlyInstanceOf(ExpireAtException.class);
    }

    @Test
    void removeQualification_ShouldRemoveFromEmployee() {
        employeeWithEmptyQAndEmptyV.getQualifications().add(qualification);
        when(employeeRepository.findById(any(UUID.class))).thenReturn(Optional.of(employeeWithEmptyQAndEmptyV));
        when(qualificationAPI.getQualification(any(UUID.class))).thenReturn(qualification);

        employeeService.removeQualification(employeeId, qualificationId);

        assertThat(employeeWithEmptyQAndEmptyV.getQualifications()).isEmpty();
    }

    @Test
    void addMachine_ShouldAddMachineToEmployee() {
        machine.setId(machineId);
        when(employeeRepository.findById(any(UUID.class))).thenReturn(Optional.of(employeeWithEmptyQAndEmptyV));
        when(machineAPI.getMachine(any(UUID.class))).thenReturn(machine);

        EmployeeDTO result = employeeService.addMachine(employeeId, machineId);

        assertThat(result.machines())
                .contains(MachineDTO.builder().id(machine.getId()).build());
    }

    @Test
    void removeMachine_ShouldRemoveMachineFromEmployee() {
        employeeWithEmptyQAndEmptyV.getMachines().add(machine);
        when(employeeRepository.findById(any(UUID.class))).thenReturn(Optional.of(employeeWithEmptyQAndEmptyV));
        when(machineAPI.getMachine(any(UUID.class))).thenReturn(machine);

        employeeService.removeMachine(employeeId, machineId);

        assertThat(employeeWithEmptyQAndEmptyV.getMachines()).doesNotContain(machine);
    }

    @Test
    void getAllEmployeeQualificationsWithExpirationTime_ShouldReturnDTOs() {
        employeeWithQAndV.setId(employeeId);
        qualification.setId(qualificationId);

        var eq = new EmployeeQualification();
        eq.setId(new EmployeeQualificationId(employeeId, qualificationId));
        eq.setEmployee(employeeWithQAndV);
        eq.setQualification(qualification);
        eq.setExpiredAt(Instant.now().plusSeconds(3600));

        when(employeeQualificationRepository.findAllByEmployeeIdAndExpiredAtIsNotNull(any(UUID.class)))
                .thenReturn(Set.of(eq));

        when(employeeMapper.toDTO(any(EmployeeQualification.class))).thenAnswer(invocation -> {
            EmployeeQualification eQ = invocation.getArgument(0);
            return EmployeeQualificationDTO.builder()
                    .employeeId(eQ.getEmployee().getId())
                    .qualificationId(eQ.getQualification().getId())
                    .expiredAt(
                            eQ.getExpiredAt() != null
                                    ? eQ.getExpiredAt()
                                            .atZone(ZoneId.systemDefault())
                                            .toLocalDate()
                                    : null)
                    .build();
        });

        List<EmployeeQualificationDTO> result =
                employeeService.getAllEmployeeQualificationsWithExpirationTime(employeeId);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).qualificationId()).isEqualTo(qualificationId);
        assertThat(result.get(0).expiredAt()).isNotNull();
    }

    @Test
    void getAllEmployeeQualificationsWithExpirationTime_ShouldReturnEmptyList_whenNoneExist() {
        when(employeeQualificationRepository.findAllByEmployeeIdAndExpiredAtIsNotNull(any(UUID.class)))
                .thenReturn(Set.of());

        List<EmployeeQualificationDTO> result =
                employeeService.getAllEmployeeQualificationsWithExpirationTime(employeeId);

        assertThat(result).isEmpty();
    }
}
