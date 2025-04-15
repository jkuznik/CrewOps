package pl.crewops.domain.vehicle;

import static pl.crewops.enums.ControllerURL.*;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.crewops.dto.vehicle.CreateVehicleDTO;
import pl.crewops.dto.vehicle.UpdateVehicleDTO;
import pl.crewops.dto.vehicle.VehicleDTO;

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

    @PostMapping(VEHICLES_VIDS)
    public ResponseEntity<List<VehicleDTO>> getVehiclesByIds(@RequestBody @Valid @NotNull Set<UUID> vehicleIds) {
        return ResponseEntity.status(HttpStatus.OK).body(vehicleService.getVehiclesIn(vehicleIds));
    }

    @PatchMapping(VEHICLES_VID)
    public ResponseEntity<VehicleDTO> updateVehicle(
            @PathVariable(VEHICLE_ID) UUID vehicleId,
            @RequestParam(required = false) String registerNumber,
            @RequestParam(required = false) Boolean broken) {
        var updateVehicleDTO = new UpdateVehicleDTO(vehicleId, registerNumber, broken);

        return ResponseEntity.status(HttpStatus.OK).body(vehicleService.updateVehicle(updateVehicleDTO));
    }

    @DeleteMapping(VEHICLES_VID)
    public ResponseEntity<VehicleDTO> deleteVehicle(@PathVariable(VEHICLE_ID) UUID vehicleId) {
        vehicleService.deleteVehicle(vehicleId);
        return ResponseEntity.status(HttpStatus.OK).build();
    }
}
