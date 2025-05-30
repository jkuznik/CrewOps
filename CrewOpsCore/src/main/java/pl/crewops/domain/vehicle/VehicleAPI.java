package pl.crewops.domain.vehicle;

import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import pl.crewops.dto.vehicle.CreateVehicleDTO;
import pl.crewops.dto.vehicle.UpdateVehicleDTO;
import pl.crewops.dto.vehicle.VehicleDTO;
import pl.crewops.exception.VehicleNotFoundException;
import pl.crewops.model.Vehicle;

@Component
@RequiredArgsConstructor
public class VehicleAPI {

    private final VehicleService vehicleService;

    public VehicleDTO createVehicle(CreateVehicleDTO createVehicleDTO) {
        return vehicleService.createVehicle(createVehicleDTO);
    }

    public Vehicle getVehicle(UUID vehicleId) throws VehicleNotFoundException {
        return vehicleService.getVehicleById(vehicleId);
    }

    public VehicleDTO updateVehicle(UpdateVehicleDTO updateVehicleDTO) throws VehicleNotFoundException {
        return vehicleService.updateVehicle(updateVehicleDTO);
    }
}
