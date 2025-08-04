package pl.crewops.domain.machine;

import pl.crewops.dto.machine.CreateMachineDTO;
import pl.crewops.dto.machine.MachineDTO;
import pl.crewops.dto.machineType.MachineTypeDTO;
import pl.crewops.model.Machine;

class MachineMapper {

    public static Machine mapToEntity(CreateMachineDTO createMachineDTO) {
        return Machine.builder()
                .make(createMachineDTO.make())
                .model(createMachineDTO.model())
                .year(createMachineDTO.year())
                .vin(createMachineDTO.vin())
                .registerNumber(createMachineDTO.registerNumber())
                .broken(createMachineDTO.broken())
                .build();
    }

    public static MachineDTO mapToDTO(Machine machine) {
        return MachineDTO.builder()
                .id(machine.getId())
                .make(machine.getMake())
                .model(machine.getModel())
                .machineType(MachineTypeDTO.builder()
                        .id(machine.getMachineType().getId())
                        .name(machine.getMachineType().getName())
                        .build())
                .year(machine.getYear())
                .vin(machine.getVin())
                .registerNumber(machine.getRegisterNumber())
                .broken(machine.getBroken())
                .build();
    }
}
