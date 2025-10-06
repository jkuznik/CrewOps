package pl.crewops.domain.machineType;

import pl.crewops.model.dto.machineType.CreateMachineTypeDTO;
import pl.crewops.model.dto.machineType.MachineTypeDTO;
import pl.crewops.model.tenantSchema.MachineType;

class MachineTypeMapper {

    public static MachineType mapToEntity(CreateMachineTypeDTO createMachineTypeDTO) {
        return MachineType.builder().name(createMachineTypeDTO.name()).build();
    }

    public static MachineTypeDTO mapToDTO(MachineType machineType) {
        return MachineTypeDTO.builder().name(machineType.getName()).build();
    }
}
