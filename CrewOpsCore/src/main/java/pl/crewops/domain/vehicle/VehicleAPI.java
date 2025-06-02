package pl.crewops.domain.vehicle;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;
import pl.crewops.dto.vehicle.CreateVehicleDTO;
import pl.crewops.dto.vehicle.UpdateVehicleDTO;
import pl.crewops.dto.vehicle.VehicleDTO;
import pl.crewops.exception.VehicleNotFoundException;
import pl.crewops.model.Vehicle;

@Component
@Validated
public interface VehicleAPI {

    VehicleDTO createVehicle(@NotNull @Valid CreateVehicleDTO createVehicleDTO);

    Vehicle getVehicle(@NotNull UUID vehicleId) throws VehicleNotFoundException;

    VehicleDTO updateVehicle(@NotNull @Valid UpdateVehicleDTO updateVehicleDTO) throws VehicleNotFoundException;

    List<VehicleDTO> getAllVehicles(int page, int size);

    VehicleDTO getVehicleByRegistrationNumber(@NotNull String registrationNumber);

    List<VehicleDTO> getVehiclesIn(@NotNull Set<UUID> vehicleIds);

    void deleteVehicle(@NotNull UUID vehicleId);
}
