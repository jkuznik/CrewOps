package pl.crewops.model;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.UUID;
import lombok.*;
import pl.crewops.dto.vehicle.CreateVehicleDTO;
import pl.crewops.dto.vehicle.UpdateVehicleDTO;
import pl.crewops.dto.vehicle.VehicleDTO;
import pl.crewops.enums.VehicleType;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VehicleFormModel {
    private UUID id;
    private @NotNull @Size(min = 2, max = 31, message = "Length required between 2-31") String make;
    private @NotNull @Size(min = 2, max = 31, message = "Length required between 2-31") String model;
    private @NotNull String vehicleType;
    private @NotNull Integer year;
    private @Size(min = 2, max = 50, message = "Length required between 2-50") String vin;
    private @Size(min = 2, max = 15, message = "Length required between 2-15") String registrationNumber;
    private @NotNull Boolean broken;

    public static CreateVehicleDTO toCreateVehicleDTO(VehicleFormModel vehicleFormModel) {
        return CreateVehicleDTO.builder()
                .make(vehicleFormModel.getMake())
                .model(vehicleFormModel.getModel())
                //                .vehicleType(VehicleType.valueOf(vehicleFormModel.getVehicleType()))
                .vehicleType(VehicleType.SEMI_TRUCK)
                .year(vehicleFormModel.getYear())
                .vin(vehicleFormModel.getVin())
                .registerNumber(vehicleFormModel.getRegistrationNumber())
                .broken(vehicleFormModel.getBroken())
                .build();
    }

    public static VehicleFormModel toVehicleFormModel(VehicleDTO vehicleDTO) {
        return VehicleFormModel.builder()
                .id(vehicleDTO.id())
                .make(vehicleDTO.make())
                .model(vehicleDTO.model())
                .vehicleType(vehicleDTO.vehicleType().name())
                .year(vehicleDTO.year())
                .vin(vehicleDTO.vin())
                .registrationNumber(vehicleDTO.registerNumber())
                .broken(vehicleDTO.broken())
                .build();
    }

    public static UpdateVehicleDTO toUpdateVehicleDTO(VehicleFormModel vehicleFormModel) {
        return UpdateVehicleDTO.builder()
                .vehicleId(vehicleFormModel.getId())
                .registerNumber(vehicleFormModel.getRegistrationNumber())
                .broken(vehicleFormModel.getBroken())
                .build();
    }
}
