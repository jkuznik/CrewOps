package pl.crewops.domain.machine;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import org.springframework.validation.annotation.Validated;
import pl.crewops.exception.domain.machine.MachineNotFoundException;
import pl.crewops.model.dto.machine.CreateMachineDTO;
import pl.crewops.model.dto.machine.MachineDTO;
import pl.crewops.model.dto.machine.UpdateMachineDTO;
import pl.crewops.model.tenantSchema.Machine;

@Validated
public interface MachineAPI {

    MachineDTO createMachine(@NotNull @Valid CreateMachineDTO createMachineDTO);

    Machine getMachine(@NotNull UUID machineId) throws MachineNotFoundException;

    MachineDTO updateMachine(@NotNull @Valid UpdateMachineDTO updateMachineDTO) throws MachineNotFoundException;

    List<MachineDTO> getAllMachines(int page, int size);

    MachineDTO getMachineByRegistrationNumber(@NotNull String registrationNumber);

    List<MachineDTO> getMachinesIn(@NotNull Set<UUID> machineIds);

    void deleteMachine(@NotNull UUID vachineId);
}
