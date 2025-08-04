package pl.crewops.domain.machineType;

import pl.crewops.dto.machineType.CreateMachineTypeDTO;
import pl.crewops.dto.machineType.MachineTypeDTO;
import pl.crewops.model.MachineType;

class MachineTypeTestFactory {

    public static CreateMachineTypeDTO createCreateMachineTypeDTO() {
        return CreateMachineTypeDTO.builder().name("name").build();
    }

    public static MachineType createMachineType() {
        return MachineType.builder().name("name").build();
    }

    public static MachineTypeDTO createMachineTypeDTO() {
        return MachineTypeDTO.builder().name("name").build();
    }
}
