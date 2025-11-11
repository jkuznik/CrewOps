package pl.crewops.domain.shift;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import pl.crewops.enums.ControllerURL;
import pl.crewops.model.dto.shift.CreateShiftDTO;
import pl.crewops.model.dto.shift.ShiftDTO;

@RestController
@RequiredArgsConstructor
class ShiftController {

    private final ShiftAPI shiftAPI;

    @PostMapping(ControllerURL.SHIFTS)
    public ResponseEntity<ShiftDTO> createShift(@RequestBody CreateShiftDTO createShiftDTO) {
        return ResponseEntity.status(HttpStatus.CREATED).body(shiftAPI.createShift(createShiftDTO));
    }
}
