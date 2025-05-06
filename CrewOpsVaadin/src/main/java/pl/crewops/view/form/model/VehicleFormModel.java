package pl.crewops.view.form.model;

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
    private String make;
    private String model;
    private String vehicleType;
    private Integer year;
    private String vin;
    private String registerNumber;
    private Boolean broken;

    public static CreateVehicleDTO toCreateVehicleDTO(VehicleFormModel vehicleFormModel) {
        return CreateVehicleDTO.builder()
                .make(vehicleFormModel.getMake())
                .model(vehicleFormModel.getModel())
                //                .vehicleType(VehicleType.valueOf(vehicleFormModel.getVehicleType()))
                .vehicleType(VehicleType.SEMI_TRUCK)
                .year(vehicleFormModel.getYear())
                .vin(vehicleFormModel.getVin())
                .registerNumber(vehicleFormModel.getRegisterNumber())
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
                .registerNumber(vehicleDTO.registerNumber())
                .broken(vehicleDTO.broken())
                .build();
    }

    public static UpdateVehicleDTO toUpdateVehicleDTO(VehicleFormModel vehicleFormModel) {
        return UpdateVehicleDTO.builder()
                .vehicleId(vehicleFormModel.getId())
                .registerNumber(vehicleFormModel.getRegisterNumber())
                .broken(vehicleFormModel.getBroken())
                .build();
    }
}
