package pl.kuznik.domain.vehicle;

import static pl.kuznik.utils.enums.ControllerURL.*;

import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.kuznik.domain.vehicle.dto.CreateVehicleDTO;
import pl.kuznik.domain.vehicle.dto.UpdateVehicleDTO;
import pl.kuznik.domain.vehicle.dto.VehicleDTO;

@RestController
@RequiredArgsConstructor
class VehicleController {

    private final VehicleService vehicleService;

    @PostMapping(VEHICLES)
    public ResponseEntity<VehicleDTO> createVehicle(@RequestBody CreateVehicleDTO createVehicleDTO) {
        return ResponseEntity.status(HttpStatus.OK).body(vehicleService.createVehicle(createVehicleDTO));
    }

    @GetMapping(VEHICLES)
    public ResponseEntity<List<VehicleDTO>> getAllVehicles(
            @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "15") int size) {
        return ResponseEntity.status(HttpStatus.OK).body(vehicleService.getAllVehicles(page, size));
    }

    @PatchMapping(VEHICLES_VID)
    public ResponseEntity<VehicleDTO> updateVehicle(
            @PathVariable(VEHICLE_ID) UUID vehicleId,
            @RequestParam(required = false) String registerNumber,
            @RequestParam(required = false) Boolean broken) {
        var updateVehicleDTO = new UpdateVehicleDTO(vehicleId, registerNumber, broken);

        return ResponseEntity.status(HttpStatus.OK).body(vehicleService.updateVehicle(updateVehicleDTO));
    }
}
