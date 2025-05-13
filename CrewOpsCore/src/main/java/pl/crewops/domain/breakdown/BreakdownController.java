package pl.crewops.domain.breakdown;

import static pl.crewops.enums.ControllerURL.BREAKDOWNS;

import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.crewops.dto.breakdown.BreakdownDTO;
import pl.crewops.dto.breakdown.CreateBreakdownDTO;

@RestController
@Slf4j
@RequiredArgsConstructor
public class BreakdownController {
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
}
