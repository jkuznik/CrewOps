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
import pl.crewops.model.dto.machine.CreateMachineDTO;
import pl.crewops.model.dto.machine.MachineDTO;
import pl.crewops.model.dto.machine.UpdateMachineDTO;
import pl.crewops.model.dto.machineType.MachineTypeDTO;
import pl.crewops.model.tenantSchema.Machine;
import pl.crewops.model.tenantSchema.MachineType;

@SpringJUnitConfig(classes = {MachineService.class})
class MachineServiceTest {

    @MockitoBean
    private MachineMapper mapper;

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

        when(mapper.toEntity(any(CreateMachineDTO.class))).thenReturn(machine);
        when(machineRepository.save(any(Machine.class))).thenReturn(machine);

        when(mapper.toDTO(any(Machine.class)))
                .thenReturn(MachineDTO.builder()
                        .id(machine.getId())
                        .make("make")
                        .model("model")
                        .machineType(new MachineTypeDTO(machineType.getId(), machineType.getName()))
                        .build());

        MachineDTO result = machineService.createMachine(createMachineDTO);

        assertThat(result.make()).isEqualTo("make");
        assertThat(result.model()).isEqualTo("model");
    }

    @Test
    void shouldReturnListOfMachines_whenMachinesExist() {
        Page<Machine> machines = new PageImpl<>(Collections.singletonList(machine));
        when(machineRepository.findAll(any(PageRequest.class))).thenReturn(machines);

        when(mapper.toDTO(any(Machine.class)))
                .thenReturn(MachineDTO.builder()
                        .id(machine.getId())
                        .make("make")
                        .model("model")
                        .machineType(new MachineTypeDTO(machineType.getId(), machineType.getName()))
                        .build());

        List<MachineDTO> result = machineService.getAllMachines(0, 5);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).make()).isEqualTo("make");
    }

    @Test
    void shouldReturnMachine_whenMachineExists() {
        when(machineRepository.findById(any(UUID.class))).thenReturn(Optional.of(machine));

        Machine result = machineService.getMachineById(machineId);

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

        when(mapper.toDTO(any(Machine.class)))
                .thenReturn(MachineDTO.builder()
                        .id(machine.getId())
                        .registerNumber(updateMachineDTOValid.registerNumber())
                        .make(machine.getMake())
                        .model(machine.getModel())
                        .machineType(new MachineTypeDTO(machineType.getId(), machineType.getName()))
                        .build());

        MachineDTO result = machineService.updateMachine(updateMachineDTOValid);

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
        when(machineRepository.countByMachineType(any())).thenReturn(1);

        doNothing().when(machineRepository).deleteById(machineId);
        doNothing().when(machineTypeAPI).delete(machineType);

        machineService.deleteMachine(machineId);

        // then
        verify(machineRepository, times(1)).deleteById(machineId);
        verify(machineTypeAPI, times(1)).delete(machineType);
    }

    @Test
    void shouldReturnMachineDTO_whenMachineExistsByRegistrationNumber() {
        when(machineRepository.findByRegisterNumber("registerNumber")).thenReturn(Optional.of(machine));

        when(mapper.toDTO(any(Machine.class)))
                .thenReturn(MachineDTO.builder()
                        .id(machine.getId())
                        .registerNumber("registerNumber")
                        .make(machine.getMake())
                        .model(machine.getModel())
                        .machineType(new MachineTypeDTO(machineType.getId(), machineType.getName()))
                        .build());

        MachineDTO result = machineService.getMachineByRegistrationNumber("registerNumber");

        assertThat(result.registerNumber()).isEqualTo("registerNumber");
    }

    @Test
    void shouldReturnListOfMachines_whenMachinesExistInIds() {
        UUID id1 = UUID.randomUUID();
        UUID id2 = UUID.randomUUID();

        Machine m1 = machine();
        m1.setId(id1);
        Machine m2 = machine();
        m2.setId(id2);

        when(machineRepository.findAllByIdIn(Set.of(id1, id2))).thenReturn(Set.of(m1, m2));

        when(mapper.toDTO(any(Machine.class))).thenAnswer(invocation -> {
            Machine source = invocation.getArgument(0);
            return MachineDTO.builder()
                    .id(source.getId())
                    .make(source.getMake())
                    .model(source.getModel())
                    .machineType(new MachineTypeDTO(machineType.getId(), machineType.getName()))
                    .build();
        });

        List<MachineDTO> result = machineService.getMachinesIn(Set.of(id1, id2));

        assertThat(result.stream().map(MachineDTO::id)).containsExactlyInAnyOrder(id1, id2);
    }
}
