package pl.crewops.domain.dailyEntry;

import static pl.crewops.enums.ControllerURL.DAILY_ENTRIES;

import java.time.LocalDate;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import pl.crewops.model.dto.dailyEntry.CreateDailyEntryDTO;
import pl.crewops.model.dto.dailyEntry.DailyEntryDTO;

@RestController
@RequiredArgsConstructor
class DailyEntryController {

    private final DailyEntryAPI dailyEntryAPI;

    @PostMapping(DAILY_ENTRIES)
    public DailyEntryDTO createDailyEntry(@RequestBody CreateDailyEntryDTO createDailyEntryDTO) {
        return dailyEntryAPI.createDailyEntry(createDailyEntryDTO);
    }

    @GetMapping(DAILY_ENTRIES)
    public DailyEntryDTO getDailyEntry(@RequestParam UUID employeeId, @RequestParam LocalDate entryDate) {
        return dailyEntryAPI.getByEmployeeIdAndEntryDate(employeeId, entryDate);
    }
}
