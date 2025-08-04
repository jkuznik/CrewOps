package pl.crewops.domain.machine;

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
import pl.crewops.dto.machine.CreateMachineDTO;
import pl.crewops.dto.machine.MachineDTO;
import pl.crewops.dto.machine.UpdateMachineDTO;
import pl.crewops.security.custom.permissionAnnotation.ManagerPermission;
import pl.crewops.security.custom.permissionAnnotation.MechanicPermission;

@RestController
@RequiredArgsConstructor
class MachineController {

    private final MachineAPI machineAPI;

    @PostMapping(MACHINES)
    @ManagerPermission
    public ResponseEntity<MachineDTO> createMachine(@NotNull @Valid @RequestBody CreateMachineDTO createMachineDTO) {
        return ResponseEntity.status(HttpStatus.OK).body(machineAPI.createMachine(createMachineDTO));
    }

    @GetMapping(MACHINES)
    public ResponseEntity<List<MachineDTO>> getAllMachines(
            @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "15") int size) {
        return ResponseEntity.status(HttpStatus.OK).body(machineAPI.getAllMachines(page, size));
    }

    @GetMapping(MACHINES_RN)
    public ResponseEntity<MachineDTO> getMachineByRegistrationNumber(@PathVariable String registrationNumber) {
        return ResponseEntity.status(HttpStatus.OK).body(machineAPI.getMachineByRegistrationNumber(registrationNumber));
    }

    @PostMapping(MACHINES_VIDS)
    public ResponseEntity<List<MachineDTO>> getMachinesByIds(@RequestBody @NotNull Set<UUID> machineIds) {
        return ResponseEntity.status(HttpStatus.OK).body(machineAPI.getMachinesIn(machineIds));
    }

    @PatchMapping(MACHINES_VID)
    @MechanicPermission
    public ResponseEntity<MachineDTO> updateMachine(
            @PathVariable(MACHINE_ID) UUID machineId, @RequestBody @Valid @NotNull UpdateMachineDTO updateRequest) {

        if (!updateRequest.machineId().equals(machineId)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Path ID and body ID must match");
        }

        var updateMachineDTO = UpdateMachineDTO.builder()
                .machineId(machineId)
                .registerNumber(updateRequest.registerNumber())
                .broken(updateRequest.broken())
                .build();

        return ResponseEntity.status(HttpStatus.OK).body(machineAPI.updateMachine(updateMachineDTO));
    }

    @DeleteMapping(MACHINES_VID)
    @ManagerPermission
    public ResponseEntity<MachineDTO> deleteMachine(@PathVariable(MACHINE_ID) UUID machineId) {
        machineAPI.deleteMachine(machineId);
        return ResponseEntity.status(HttpStatus.OK).build();
    }
}
