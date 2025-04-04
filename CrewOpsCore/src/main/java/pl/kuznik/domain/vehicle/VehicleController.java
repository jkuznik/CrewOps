package pl.kuznik.domain.vehicle;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import pl.kuznik.domain.vehicle.dto.CreateVehicleDTO;
import pl.kuznik.domain.vehicle.dto.VehicleDTO;
import pl.kuznik.utils.enums.ControllerURL;

@RestController
@RequiredArgsConstructor
class VehicleController {

    private final VehicleService vehicleService;

    @PostMapping(ControllerURL.VEHICLES)
    public ResponseEntity<VehicleDTO> createVehicle(@RequestBody CreateVehicleDTO createVehicleDTO) {
        return ResponseEntity.status(HttpStatus.OK).body(vehicleService.createVehicle(createVehicleDTO));
    }
}
