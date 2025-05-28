package pl.crewops.domain.vehicle;

import java.util.UUID;
import pl.crewops.dto.vehicle.CreateVehicleDTO;
import pl.crewops.dto.vehicle.UpdateVehicleDTO;
import pl.crewops.model.Vehicle;
import pl.crewops.model.VehicleType;

class VehicleTestFactory {

    public static Vehicle createVehicle() {
        return Vehicle.builder()
                .vehicleType(VehicleType.builder().type("ImplementThis").build())
                .make("make")
                .model("model")
                .year(2020)
                .vin("vin")
                .registerNumber("registerNumber")
                .broken(false)
                .build();
    }

    public static CreateVehicleDTO createVehicleDTO() {
        return CreateVehicleDTO.builder()
                .vehicleType(VehicleType.builder().type("ImplementThis").build().toDTO())
                .make("make")
                .model("model")
                .year(2020)
                .vin("vin")
                .registerNumber("registerNumber")
                .broken(false)
                .build();
    }

    public static CreateVehicleDTO createVehicleDTONotValid() {
        return CreateVehicleDTO.builder()
                .vehicleType(VehicleType.builder().type("ImplementThis").build().toDTO())
                .make(null)
                .model("model")
                .year(2020)
                .vin("vin")
                .registerNumber("registerNumber")
                .broken(false)
                .build();
    }

    public static UpdateVehicleDTO updateVehicleDTO() {
        return UpdateVehicleDTO.builder()
                .vehicleId(UUID.fromString("11111111-1111-1111-1111-111111111111"))
                .registerNumber("registerNumber")
                .broken(false)
                .build();
    }

    public static UpdateVehicleDTO updateVehicleDTONotValid() {
        return UpdateVehicleDTO.builder()
                .vehicleId(null)
                .registerNumber("registerNumber")
                .broken(false)
                .build();
    }
}
