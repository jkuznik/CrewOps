package pl.crewops.domain.vehicle;

import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import pl.crewops.exception.VehicleNotFoundException;
import pl.crewops.model.Vehicle;

@Component
@RequiredArgsConstructor
public class VehicleAPI {

    private final VehicleService vehicleService;

    public Vehicle getVehicle(UUID vehicleId) throws VehicleNotFoundException {
        return vehicleService.getVehicleById(vehicleId);
    }
}
