package pl.crewops.domain.machineType;

import pl.crewops.dto.machineType.CreateMachineTypeDTO;
import pl.crewops.dto.machineType.MachineTypeDTO;
import pl.crewops.model.MachineType;

class MachineTypeMapper {

    public static MachineType mapToEntity(CreateMachineTypeDTO createMachineTypeDTO) {
        return MachineType.builder().name(createMachineTypeDTO.name()).build();
    }

    public static MachineTypeDTO mapToDTO(MachineType machineType) {
        return MachineTypeDTO.builder().name(machineType.getName()).build();
    }
}
