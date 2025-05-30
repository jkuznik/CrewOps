package pl.crewops.domain.vehicleType;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.crewops.dto.vehicleType.VehicleTypeDTO;
import pl.crewops.enums.ControllerURL;

@RestController
@RequiredArgsConstructor
class VehicleTypeController {

    private final VehicleTypeAPI vehicleTypeAPI;

    @GetMapping(ControllerURL.VEHICLE_TYPES)
    public ResponseEntity<List<VehicleTypeDTO>> getAllVehicleTypes() {
        return ResponseEntity.status(HttpStatus.OK).body(vehicleTypeAPI.getAllVehicleTypes());
    }
}
