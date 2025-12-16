package pl.crewops.domain.machineType;

import org.mapstruct.Mapper;
import pl.crewops.model.dto.machineType.CreateMachineTypeDTO;
import pl.crewops.model.dto.machineType.MachineTypeDTO;
import pl.crewops.model.tenantSchema.MachineType;

@Mapper(componentModel = "spring")
public interface MachineTypeMapper {

    MachineType toEntity(CreateMachineTypeDTO dto);

    MachineTypeDTO toDTO(MachineType entity);
}
