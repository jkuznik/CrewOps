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
import org.springframework.web.server.ResponseStatusException;
import pl.crewops.dto.vehicle.CreateVehicleDTO;
import pl.crewops.dto.vehicle.UpdateVehicleDTO;
import pl.crewops.dto.vehicle.VehicleDTO;
import pl.crewops.security.custom.permissionAnnotation.ManagerPermission;
import pl.crewops.security.custom.permissionAnnotation.MechanicPermission;

@RestController
@RequiredArgsConstructor
class VehicleController {

    private final VehicleAPI vehicleAPI;

    @PostMapping(VEHICLES)
    @ManagerPermission
    public ResponseEntity<VehicleDTO> createVehicle(@NotNull @Valid @RequestBody CreateVehicleDTO createVehicleDTO) {
        return ResponseEntity.status(HttpStatus.OK).body(vehicleAPI.createVehicle(createVehicleDTO));
    }

    @GetMapping(VEHICLES)
    public ResponseEntity<List<VehicleDTO>> getAllVehicles(
            @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "15") int size) {
        return ResponseEntity.status(HttpStatus.OK).body(vehicleAPI.getAllVehicles(page, size));
    }

    @GetMapping(VEHICLES_RN)
    public ResponseEntity<VehicleDTO> getVehicleByRegistrationNumber(@PathVariable String registrationNumber) {
        return ResponseEntity.status(HttpStatus.OK).body(vehicleAPI.getVehicleByRegistrationNumber(registrationNumber));
    }

    @PostMapping(VEHICLES_VIDS)
    public ResponseEntity<List<VehicleDTO>> getVehiclesByIds(@RequestBody @NotNull Set<UUID> vehicleIds) {
        return ResponseEntity.status(HttpStatus.OK).body(vehicleAPI.getVehiclesIn(vehicleIds));
    }

    @PatchMapping(VEHICLES_VID)
    @MechanicPermission
    public ResponseEntity<VehicleDTO> updateVehicle(
            @PathVariable(VEHICLE_ID) UUID vehicleId, @RequestBody @Valid @NotNull UpdateVehicleDTO updateRequest) {

        if (!updateRequest.vehicleId().equals(vehicleId)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Path ID and body ID must match");
        }

        var updateVehicleDTO = UpdateVehicleDTO.builder()
                .vehicleId(vehicleId)
                .registerNumber(updateRequest.registerNumber())
                .broken(updateRequest.broken())
                .build();

        return ResponseEntity.status(HttpStatus.OK).body(vehicleAPI.updateVehicle(updateVehicleDTO));
    }

    @DeleteMapping(VEHICLES_VID)
    @ManagerPermission
    public ResponseEntity<VehicleDTO> deleteVehicle(@PathVariable(VEHICLE_ID) UUID vehicleId) {
        vehicleAPI.deleteVehicle(vehicleId);
        return ResponseEntity.status(HttpStatus.OK).build();
    }
}
