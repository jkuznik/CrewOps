package pl.crewops.domain.machine;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchException;
import static pl.crewops.domain.machine.MachineTestFactory.createMachineDTONotValid;
import static pl.crewops.domain.machine.MachineTestFactory.updateMachineDTONotValid;

import jakarta.validation.ConstraintViolationException;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.transaction.annotation.Transactional;
import pl.crewops.IntegrationTest;
import pl.crewops.dto.machine.CreateMachineDTO;
import pl.crewops.dto.machineType.MachineTypeDTO;
import pl.crewops.exception.domain.machine.MachineNotFoundException;
import pl.crewops.model.Machine;
import pl.crewops.model.MachineType;

@Transactional
class MachineAPITest extends IntegrationTest {

    @Test
    void shouldReturnMachine_whenMachineExists() {
        // given
        var machineId = UUID.fromString("33333333-3333-3333-3333-333333333333");

        // when
        var machine = machineAPI.getMachine(machineId);

        // then
        assertThat(machine).isNotNull();
        assertThat(machine.getId()).isEqualTo(machineId);
        assertThat(machine.getMachineType().getName()).isEqualTo("BULLDOZER");
    }

    @Test
    void createMachine_shouldReturnMachine_whenCreateMachineDTOIsValid_andMachineTypeIsExist() {
        // given
        var createMachineDTO = CreateMachineDTO.builder()
                .make("make")
                .model("model")
                .machineType(MachineTypeDTO.builder().name("LOADER").build())
                .year(2020)
                .registerNumber("number")
                .broken(false)
                .build();

        // when
        var machine = machineAPI.createMachine(createMachineDTO);

        Machine result = machineAPI.getMachine(machine.id());

        // then
        assertThat(result).isNotNull();
        assertThat(result.getMachineType().getId()).isEqualTo(UUID.fromString("00000000-0000-0000-0000-000000000004"));
    }

    @Test
    void createMachine_shouldReturnMachine_whenCreateMachineDTOIsValid_andMachineTypeIsNotExist() {
        // given
        var createMachineDTO = CreateMachineDTO.builder()
                .make("make")
                .model("model")
                .machineType(MachineTypeDTO.builder().name("NEW TYPE").build())
                .year(2020)
                .registerNumber("number")
                .broken(false)
                .build();

        // when
        var machine = machineAPI.createMachine(createMachineDTO);

        Machine result = machineAPI.getMachine(machine.id());

        // then
        assertThat(result).isNotNull();
        assertThat(result.getMachineType()).isNotNull();
        assertThat(result.getMachineType().getName()).isEqualTo("NEW TYPE");
    }

    @Test
    void shouldThrowException_whenCreateMachineDTOIsNull() {
        Exception result = catchException(() -> machineAPI.createMachine(null));

        assertThat(result).isInstanceOf(ConstraintViolationException.class);
    }

    @Test
    void shouldThrowException_whenCreateMachineDTOIsInvalid() {
        // given
        var createMachineDTONotValid = createMachineDTONotValid();

        // when
        Exception result = catchException(() -> machineAPI.createMachine(createMachineDTONotValid));

        assertThat(result).isInstanceOf(ConstraintViolationException.class);
    }

    @Test
    void shouldThrowException_whenUpdateMachineDTOIsInvalid() {
        // given
        var updateMachineDTONotValid = updateMachineDTONotValid();

        // when
        Exception result = catchException(() -> machineAPI.updateMachine(updateMachineDTONotValid));

        assertThat(result).isInstanceOf(ConstraintViolationException.class);
    }

    @Test
    void deleteMachine_shouldDeleteMachine_whenMachineExists() {
        // given
        var machineId = UUID.fromString("33333333-3333-3333-3333-333333333333");

        // when
        machineAPI.deleteMachine(machineId);

        Exception result = catchException(() -> machineAPI.getMachine(machineId));

        // then
        assertThat(result).isInstanceOf(MachineNotFoundException.class);
    }

    @Test
    void deleteMachine_shouldDeleteMachineAndMachineType_whenExistOnlyOneMachineWithCurrentMachineType() {
        // given
        var machineId = UUID.fromString("77777777-cccc-cccc-cccc-cccccccccccc"); // only one machine with 'loader' type
        Optional<MachineType> before = machineTypeAPI.getMachineTypeByName("LOADER");

        // when
        machineAPI.deleteMachine(machineId);

        Optional<MachineType> after = machineTypeAPI.getMachineTypeByName("LOADER");
        Exception result = catchException(() -> machineAPI.getMachine(machineId));

        // then
        assertThat(before.isPresent()).isTrue();
        assertThat(after.isPresent()).isFalse();
        assertThat(result).isInstanceOf(MachineNotFoundException.class);
    }
}
