package pl.crewops.domain.vehicle;

import pl.crewops.dto.vehicle.CreateVehicleDTO;
import pl.crewops.dto.vehicle.VehicleDTO;
import pl.crewops.dto.vehicleType.VehicleTypeDTO;
import pl.crewops.model.Vehicle;

class VehicleMapper {

    public static Vehicle mapToEntity(CreateVehicleDTO createVehicleDTO) {
        return Vehicle.builder()
                .make(createVehicleDTO.make())
                .model(createVehicleDTO.model())
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
                .vehicleType(VehicleTypeDTO.builder()
                        .id(vehicle.getVehicleType().getId())
                        .name(vehicle.getVehicleType().getName())
                        .build())
                .year(vehicle.getYear())
                .vin(vehicle.getVin())
                .registerNumber(vehicle.getRegisterNumber())
                .broken(vehicle.getBroken())
                .build();
    }
}
