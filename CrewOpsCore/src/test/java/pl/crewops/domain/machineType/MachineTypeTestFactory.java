package pl.crewops.domain.machineType;

import pl.crewops.model.dto.machineType.CreateMachineTypeDTO;
import pl.crewops.model.dto.machineType.MachineTypeDTO;
import pl.crewops.model.tenantSchema.MachineType;

class MachineTypeTestFactory {

    public static CreateMachineTypeDTO createMachineTypeDTO() {
        return CreateMachineTypeDTO.builder().name("name").build();
    }

    public static MachineType machineType() {
        return MachineType.builder().name("name").build();
    }

    public static MachineTypeDTO machineTypeDTO() {
        return MachineTypeDTO.builder().name("name").build();
    }
}
