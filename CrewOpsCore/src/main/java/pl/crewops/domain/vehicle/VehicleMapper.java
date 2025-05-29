package pl.crewops.domain.vehicle;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import pl.crewops.domain.vehicleType.VehicleTypeAPI;
import pl.crewops.dto.vehicle.CreateVehicleDTO;
import pl.crewops.dto.vehicle.VehicleDTO;
import pl.crewops.dto.vehicleType.VehicleTypeDTO;
import pl.crewops.model.Vehicle;
import pl.crewops.model.VehicleType;

@Component
@RequiredArgsConstructor
class VehicleMapper {

    private final VehicleTypeAPI vehicleTypeAPI;

    public static Vehicle mapToEntity(CreateVehicleDTO createVehicleDTO) {
        var vehicleType = VehicleType.builder()
                .name(createVehicleDTO.vehicleType().name())
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
