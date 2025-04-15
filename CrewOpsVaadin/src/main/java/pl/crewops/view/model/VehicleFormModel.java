package pl.crewops.view.model;

import java.util.UUID;
import lombok.*;
import pl.crewops.dto.vehicle.CreateVehicleDTO;
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
    private String registrationNumber;
    private Boolean broken;

    public static CreateVehicleDTO toCreateVehicleDTO(VehicleFormModel vehicleFormModel) {
        return CreateVehicleDTO.builder()
                .make(vehicleFormModel.getMake())
                .model(vehicleFormModel.getModel())
                .vehicleType(VehicleType.valueOf(vehicleFormModel.getVehicleType()))
                .year(vehicleFormModel.getYear())
                .vin(vehicleFormModel.getVin())
                .registerNumber(vehicleFormModel.getRegistrationNumber())
                .broken(vehicleFormModel.getBroken())
                .build();
    }
}
