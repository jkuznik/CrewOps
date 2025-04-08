package pl.crewops.domain.vehicle;

import pl.crewops.dto.vehicle.CreateVehicleDTO;
import pl.crewops.dto.vehicle.VehicleDTO;
import pl.crewops.model.Vehicle;

class VehicleMapper {

    public static Vehicle mapToEntity(CreateVehicleDTO createVehicleDTO) {
        return Vehicle.builder()
                .make(createVehicleDTO.make())
                .model(createVehicleDTO.model())
                .vehicleType(createVehicleDTO.vehicleType())
                .year(createVehicleDTO.year())
                .vin(createVehicleDTO.vin())
                .registerNumber(createVehicleDTO.registerNumber())
                .broken(createVehicleDTO.broken())
                .build();
    }

    public static VehicleDTO mapToDTO(Vehicle vehicle) {
        return VehicleDTO.builder()
                .id(vehicle.getId())
                .make(vehicle.getMake())
                .model(vehicle.getModel())
                .vehicleTyp(vehicle.getVehicleType())
                .year(vehicle.getYear())
                .vin(vehicle.getVin())
                .registerNumber(vehicle.getRegisterNumber())
                .broken(vehicle.getBroken())
                .build();
    }
}
