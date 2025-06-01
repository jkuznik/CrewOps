package pl.crewops.domain.vehicleType;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import java.util.Optional;
import org.springframework.validation.annotation.Validated;
import pl.crewops.dto.vehicleType.CreateVehicleTypeDTO;
import pl.crewops.dto.vehicleType.VehicleTypeDTO;
import pl.crewops.model.VehicleType;

@Validated
public interface VehicleTypeAPI {

    VehicleType create(@NotNull @Valid CreateVehicleTypeDTO createVehicleTypeDTO);

    Optional<VehicleType> getVehicleTypeByName(@NotNull @NotBlank String name);

    List<VehicleTypeDTO> getAllVehicleTypes();

    void delete(@NotNull VehicleType vehicleType);
}
