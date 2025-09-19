package pl.crewops.domain.machine;

import java.util.UUID;
import pl.crewops.model.Machine;
import pl.crewops.model.MachineType;
import pl.crewops.model.dto.machine.CreateMachineDTO;
import pl.crewops.model.dto.machine.UpdateMachineDTO;
import pl.crewops.model.dto.machineType.MachineTypeDTO;

class MachineTestFactory {

    public static Machine machine() {
        return Machine.builder()
                .machineType(MachineType.builder().name("name").build())
                .make("make")
                .model("model")
                .year(2020)
                .vin("vin")
                .registerNumber("registerNumber")
                .broken(false)
                .build();
    }

    public static CreateMachineDTO createMachineDTO() {
        return CreateMachineDTO.builder()
                .machineType(MachineTypeDTO.builder().name("LOADER").build())
                .make("make")
                .model("model")
                .year(2020)
                .vin("vin")
                .registerNumber("registerNumber")
                .broken(false)
                .build();
    }

    public static CreateMachineDTO createMachineDTONotValid() {
        return CreateMachineDTO.builder()
                .machineType(MachineTypeDTO.builder().name("LOADER").build())
                .make(null)
                .model("model")
                .year(2020)
                .vin("vin")
                .registerNumber("registerNumber")
                .broken(false)
                .build();
    }

    public static UpdateMachineDTO updateMachineDTO() {
        return UpdateMachineDTO.builder()
                .machineId(UUID.fromString("11111111-1111-1111-1111-111111111111"))
                .registerNumber("registerNumber")
                .broken(false)
                .build();
    }

    public static UpdateMachineDTO updateMachineDTONotValid() {
        return UpdateMachineDTO.builder()
                .machineId(null)
                .registerNumber("registerNumber")
                .broken(false)
                .build();
    }

    public static MachineType machineType() {
        return MachineType.builder().name("name").build();
    }
}
