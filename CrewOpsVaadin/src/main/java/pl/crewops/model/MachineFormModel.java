package pl.crewops.model;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.UUID;
import lombok.*;
import pl.crewops.dto.machine.CreateMachineDTO;
import pl.crewops.dto.machine.MachineDTO;
import pl.crewops.dto.machine.UpdateMachineDTO;
import pl.crewops.dto.machineType.MachineTypeDTO;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MachineFormModel {
    private UUID id;
    private @NotNull @Size(min = 2, max = 31, message = "Length required between 2-31") String make;
    private @NotNull @Size(min = 2, max = 31, message = "Length required between 2-31") String model;
    private @NotNull String machineType;
    private @NotNull Integer year;
    private @Size(min = 2, max = 50, message = "Length required between 2-50") String vin;
    private @Size(min = 2, max = 15, message = "Length required between 2-15") String registerNumber;
    private @NotNull Boolean broken;

    public static CreateMachineDTO toCreateMachineDTO(MachineFormModel machineFormModel) {
        return CreateMachineDTO.builder()
                .make(machineFormModel.getMake())
                .model(machineFormModel.getModel())
                .machineType(MachineTypeDTO.builder()
                        .name(machineFormModel.getMachineType())
                        .build())
                .year(machineFormModel.getYear())
                .vin(machineFormModel.getVin())
                .registerNumber(machineFormModel.getRegisterNumber())
                .broken(machineFormModel.getBroken())
                .build();
    }

    public static MachineFormModel toMachineFormModel(MachineDTO machineDTO) {
        return MachineFormModel.builder()
                .id(machineDTO.id())
                .make(machineDTO.make())
                .model(machineDTO.model())
                .machineType(machineDTO.machineType().name())
                .year(machineDTO.year())
                .vin(machineDTO.vin())
                .registerNumber(machineDTO.registerNumber())
                .broken(machineDTO.broken())
                .build();
    }

    public static UpdateMachineDTO toUpdateMachineDTO(MachineFormModel machineFormModel) {
        return UpdateMachineDTO.builder()
                .machineId(machineFormModel.getId())
                .registerNumber(machineFormModel.getRegisterNumber())
                .broken(machineFormModel.getBroken())
                .build();
    }
}
