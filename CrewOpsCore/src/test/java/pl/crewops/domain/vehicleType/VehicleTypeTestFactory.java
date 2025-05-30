package pl.crewops.domain.vehicleType;

import pl.crewops.dto.vehicleType.CreateVehicleTypeDTO;
import pl.crewops.dto.vehicleType.VehicleTypeDTO;
import pl.crewops.model.VehicleType;

class VehicleTypeTestFactory {

    public static CreateVehicleTypeDTO createCreateVehicleTypeDTO() {
        return CreateVehicleTypeDTO.builder().name("name").build();
    }

    public static VehicleType createVehicleType() {
        return VehicleType.builder().name("name").build();
    }

    public static VehicleTypeDTO createVehicleTypeDTO() {
        return VehicleTypeDTO.builder().name("name").build();
    }
}
