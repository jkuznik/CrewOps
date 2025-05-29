package pl.crewops.domain.vehicleType;

import pl.crewops.dto.vehicleType.CreateVehicleTypeDTO;
import pl.crewops.dto.vehicleType.VehicleTypeDTO;
import pl.crewops.model.VehicleType;

class VehicleTypeMapper {

    public static VehicleType mapToEntity(CreateVehicleTypeDTO createVehicleTypeDTO) {
        return VehicleType.builder().name(createVehicleTypeDTO.name()).build();
    }

    public static VehicleTypeDTO mapToDTO(VehicleType vehicleType) {
        return VehicleTypeDTO.builder().name(vehicleType.getName()).build();
    }
}
