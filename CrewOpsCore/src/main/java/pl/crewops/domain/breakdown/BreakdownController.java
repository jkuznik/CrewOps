package pl.crewops.domain.breakdown;

import static pl.crewops.enums.ControllerURL.*;

import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import pl.crewops.dto.breakdown.BreakdownDTO;
import pl.crewops.dto.breakdown.CreateBreakdownDTO;
import pl.crewops.dto.breakdown.UpdateBreakdownDTO;

@RestController
@Slf4j
@RequiredArgsConstructor
class BreakdownController {
    private final BreakdownService breakdownService;

    @PostMapping(BREAKDOWNS)
    public ResponseEntity<BreakdownDTO> createBreakdown(@Valid @RequestBody CreateBreakdownDTO createBreakdownDTO) {
        return new ResponseEntity<>(breakdownService.createBreakdown(createBreakdownDTO), HttpStatus.CREATED);
    }

    @GetMapping(BREAKDOWNS)
    public ResponseEntity<List<BreakdownDTO>> getBreakdowns(
            @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "15") int size) {
        log.info("Get breakdowns");
        return ResponseEntity.status(HttpStatus.OK).body(breakdownService.getAllBreakdowns());
    }

    @PatchMapping(BREAKDOWNS_BID)
    public ResponseEntity<BreakdownDTO> updateBreakdown(
            @PathVariable(BREAKDOWN_ID) UUID breakdownId, @Valid @RequestBody UpdateBreakdownDTO updateRequest) {
        if (!updateRequest.breakdownId().equals(breakdownId)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Path ID and body ID must match");
        }

        var updateBreakdownDTO = UpdateBreakdownDTO.builder()
                .breakdownId(breakdownId)
                .repairedByEmployeeId(updateRequest.repairedByEmployeeId())
                .solved(updateRequest.solved())
                .build();

        return ResponseEntity.status(HttpStatus.OK).body(breakdownService.updateBreakdown(updateBreakdownDTO));
    }
}
