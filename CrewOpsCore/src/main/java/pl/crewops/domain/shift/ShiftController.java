package pl.crewops.domain.shift;

import static pl.crewops.enums.ControllerURL.*;

import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.crewops.model.dto.shift.CreateShiftDTO;
import pl.crewops.model.dto.shift.ShiftDTO;
import pl.crewops.model.dto.shift.UpdateShiftDTO;

@RestController
@RequiredArgsConstructor
class ShiftController {

    private final ShiftAPI shiftAPI;

    @GetMapping(SHIFTS)
    public ResponseEntity<List<ShiftDTO>> getAllShifts() {
        return ResponseEntity.ok(shiftAPI.getAllShifts());
    }

    @PostMapping(SHIFTS)
    public ResponseEntity<ShiftDTO> createShift(@RequestBody CreateShiftDTO createShiftDTO) {
        return ResponseEntity.status(HttpStatus.CREATED).body(shiftAPI.createShift(createShiftDTO));
    }

    @PutMapping(SHIFTS)
    public ResponseEntity<ShiftDTO> updateShift(@RequestBody UpdateShiftDTO updateShiftDTO) {
        return ResponseEntity.status(HttpStatus.OK).body(shiftAPI.updateShift(updateShiftDTO));
    }

    @DeleteMapping(SHIFTS_SID)
    public ResponseEntity<Void> deleteShift(@PathVariable(SHIFT_ID) UUID id) {
        shiftAPI.deleteShift(id);
        return ResponseEntity.noContent().build();
    }
}
