package pl.kuznik.domain.vehicle;

import pl.kuznik.domain.vehicle.dto.CreateVehicleDTO;
import pl.kuznik.domain.vehicle.dto.VehicleDTO;
import pl.kuznik.entity.Vehicle;

class VehicleMapper {

    public static Vehicle mapToEntity(CreateVehicleDTO createVehicleDTO) {
        return Vehicle.builder()
                .make(createVehicleDTO.make())
                .model(createVehicleDTO.model())
                .vehicleType(createVehicleDTO.type())
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
