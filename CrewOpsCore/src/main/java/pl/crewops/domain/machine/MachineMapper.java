package pl.crewops.domain.machine;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import pl.crewops.domain.machineType.MachineTypeMapper;
import pl.crewops.model.dto.machine.CreateMachineDTO;
import pl.crewops.model.dto.machine.MachineDTO;
import pl.crewops.model.tenantSchema.Machine;

@Mapper(
        componentModel = "spring",
        uses = {MachineTypeMapper.class})
public interface MachineMapper {

    @Mapping(target = "machineType", ignore = true)
    Machine toEntity(CreateMachineDTO dto);

    MachineDTO toDTO(Machine entity);
}
