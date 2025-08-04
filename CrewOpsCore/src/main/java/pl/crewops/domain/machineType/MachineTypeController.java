package pl.crewops.domain.machineType;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.crewops.dto.machineType.MachineTypeDTO;
import pl.crewops.enums.ControllerURL;

@RestController
@RequiredArgsConstructor
class MachineTypeController {

    private final MachineTypeAPI machineTypeAPI;

    @GetMapping(ControllerURL.MACHINE_TYPES)
    public ResponseEntity<List<MachineTypeDTO>> getAllMachineTypes() {
        return ResponseEntity.status(HttpStatus.OK).body(machineTypeAPI.getAllMachineTypes());
    }
}
