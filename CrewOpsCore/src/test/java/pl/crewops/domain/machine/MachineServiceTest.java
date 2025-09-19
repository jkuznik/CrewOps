package pl.crewops.domain.machine;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchException;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static pl.crewops.domain.machine.MachineTestFactory.*;

import java.util.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import pl.crewops.domain.machineType.MachineTypeAPI;
import pl.crewops.exception.domain.machine.MachineNotFoundException;
import pl.crewops.model.Machine;
import pl.crewops.model.MachineType;
import pl.crewops.model.dto.machine.CreateMachineDTO;
import pl.crewops.model.dto.machine.MachineDTO;
import pl.crewops.model.dto.machine.UpdateMachineDTO;

@SpringJUnitConfig(classes = {MachineService.class})
class MachineServiceTest {

    @MockitoBean
    private MachineRepository machineRepository;

    @MockitoBean
    private MachineTypeAPI machineTypeAPI;

    @Autowired
    private MachineService machineService;

    private Machine machine;
    private CreateMachineDTO createMachineDTO;
    private UpdateMachineDTO updateMachineDTOValid;
    private MachineType machineType;
    private final UUID machineId = UUID.randomUUID();

    @BeforeEach
    void setUp() {
        machine = machine();
        createMachineDTO = createMachineDTO();
        updateMachineDTOValid = updateMachineDTO();
        machineType = machineType();
    }

    @Test
    void shouldCreateMachine_whenCreateMachineDTOIsValid() {
        when(machineTypeAPI.getMachineTypeByName(any())).thenReturn(Optional.of(machineType));
        when(machineRepository.save(any(Machine.class))).thenReturn(machine);

        MachineDTO result = machineService.createMachine(createMachineDTO);

        assertThat(result).isNotNull();
        assertThat(result.make()).isEqualTo("make");
        assertThat(result.model()).isEqualTo("model");
        assertThat(result.machineType().name()).isEqualTo("name");
    }

    @Test
    void shouldReturnListOfMachines_whenMachinesExist() {
        Page<Machine> machines = new PageImpl<>(Collections.singletonList(machine));
        when(machineRepository.findAll(any(PageRequest.class))).thenReturn(machines);

        List<MachineDTO> result = machineService.getAllMachines(0, 5);

        assertThat(result).isNotNull();
        assertThat(result).hasSize(1);
        assertThat(result.get(0).make()).isEqualTo("make");
    }

    @Test
    void shouldReturnMachine_whenMachineExists() {
        when(machineRepository.findById(any(UUID.class))).thenReturn(Optional.of(machine));

        Machine result = machineService.getMachineById(machineId);

        assertThat(result).isNotNull();
        assertThat(result.getRegisterNumber()).isEqualTo("registerNumber");
    }

    @Test
    void shouldThrowException_whenMachineDoesNotExist() {
        when(machineRepository.findById(any(UUID.class))).thenReturn(Optional.empty());

        Exception result = catchException(() -> machineService.getMachineById(machineId));

        assertThat(result).isInstanceOf(MachineNotFoundException.class);
    }

    @Test
    void shouldUpdateMachine_whenUpdateMachineDTOIsValid() {
        when(machineRepository.findById(any(UUID.class))).thenReturn(Optional.of(machine));
        when(machineRepository.save(any(Machine.class))).thenReturn(machine);

        MachineDTO result = machineService.updateMachine(updateMachineDTOValid);

        assertThat(result).isNotNull();
        assertThat(result.registerNumber()).isEqualTo(updateMachineDTOValid.registerNumber());
    }

    @Test
    void shouldThrowException_whenMachineToUpdateDoesNotExist() {
        when(machineRepository.findById(any(UUID.class))).thenReturn(Optional.empty());

        Exception result = catchException(() -> machineService.updateMachine(updateMachineDTOValid));

        assertThat(result).isInstanceOf(MachineNotFoundException.class);
    }

    @Test
    void shouldDeleteMachine_whenIdIsValid() {
        // when
        when(machineRepository.findById(any(UUID.class))).thenReturn(Optional.of(machine));
        when(machineTypeAPI.getMachineTypeByName(any())).thenReturn(Optional.of(machineType));
        doNothing().when(machineRepository).deleteById(machineId);

        machineService.deleteMachine(machineId);

        verify(machineRepository, times(1)).deleteById(machineId);
    }

    @Test
    void shouldReturnMachineDTO_whenMachineExistsByRegistrationNumber() {
        when(machineRepository.findByRegisterNumber("registerNumber")).thenReturn(Optional.of(machine));

        MachineDTO result = machineService.getMachineByRegistrationNumber("registerNumber");

        assertThat(result).isNotNull();
        assertThat(result.registerNumber()).isEqualTo("registerNumber");
    }

    @Test
    void shouldReturnListOfMachines_whenMachinesExistInIds() {
        UUID id1 = UUID.randomUUID();
        UUID id2 = UUID.randomUUID();

        Machine machine1 = machine();
        machine1.setId(id1);

        Machine machine2 = machine();
        machine2.setId(id2);

        when(machineRepository.findAllByIdIn(Set.of(id1, id2))).thenReturn(Set.of(machine1, machine2));

        List<MachineDTO> result = machineService.getMachinesIn(Set.of(id1, id2));

        assertThat(result).hasSize(2);
        assertThat(result.stream().map(MachineDTO::id)).containsExactlyInAnyOrder(id1, id2);
    }
}
