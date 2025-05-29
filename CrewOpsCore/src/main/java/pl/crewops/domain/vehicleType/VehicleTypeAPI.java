package pl.crewops.domain.vehicleType;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.util.UUID;
import org.springframework.validation.annotation.Validated;
import pl.crewops.dto.vehicleType.CreateVehicleTypeDTO;
import pl.crewops.dto.vehicleType.VehicleTypeDTO;
import pl.crewops.model.VehicleType;

@Validated
public interface VehicleTypeAPI {

    VehicleTypeDTO create(@NotNull @Valid CreateVehicleTypeDTO createVehicleTypeDTO);

    VehicleType getById(@NotNull UUID id);
}
