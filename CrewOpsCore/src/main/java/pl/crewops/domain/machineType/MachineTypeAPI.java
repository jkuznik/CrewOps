package pl.crewops.domain.machineType;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import java.util.Optional;
import org.springframework.validation.annotation.Validated;
import pl.crewops.model.dto.machineType.CreateMachineTypeDTO;
import pl.crewops.model.dto.machineType.MachineTypeDTO;
import pl.crewops.model.tenantSchema.MachineType;

@Validated
public interface MachineTypeAPI {

    MachineType create(@NotNull @Valid CreateMachineTypeDTO createMachineTypeDTO);

    Optional<MachineType> getMachineTypeByName(@NotNull @NotBlank String name);

    List<MachineTypeDTO> getAllMachineTypes();

    void delete(@NotNull MachineType machineType);
}
