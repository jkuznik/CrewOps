package pl.crewops.domain.vehicle;

import pl.crewops.dto.vehicle.CreateVehicleDTO;
import pl.crewops.model.Vehicle;
import pl.crewops.model.VehicleType;

class VehicleMapper {

    public static Vehicle mapToEntity(CreateVehicleDTO createVehicleDTO) {
        var vehicleType = VehicleType.builder()
                .type(createVehicleDTO.vehicleType().name())
                .build();
        vehicleType.setId(createVehicleDTO.vehicleType().id());

        return Vehicle.builder()
                .make(createVehicleDTO.make())
                .model(createVehicleDTO.model())
                .vehicleType(vehicleType)
                .year(createVehicleDTO.year())
                .vin(createVehicleDTO.vin())
                .registerNumber(createVehicleDTO.registerNumber())
                .broken(createVehicleDTO.broken())
                .build();
    }

    //    public static VehicleDTO mapToDTO(Vehicle vehicle) {
    //        return VehicleDTO.builder()
    //                .id(vehicle.getId())
    //                .make(vehicle.getMake())
    //                .model(vehicle.getModel())
    //                .vehicleType(vehicle.getVehicleType().toDTO())
    //                .year(vehicle.getYear())
    //                .vin(vehicle.getVin())
    //                .registerNumber(vehicle.getRegisterNumber())
    //                .broken(vehicle.getBroken())
    //                .build();
    //    }
}
