package pl.crewops.domain.dailyEntry;

import static pl.crewops.enums.ControllerURL.DAILY_ENTRIES;

import java.time.LocalDate;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import pl.crewops.model.dto.dailyEntry.CreateDailyEntryDTO;
import pl.crewops.model.dto.dailyEntry.DailyEntryDTO;
import pl.crewops.security.custom.permissionAnnotation.SelfOnlyPermission;

@RestController
@RequiredArgsConstructor
class DailyEntryController {

    private final DailyEntryAPI dailyEntryAPI;

    @PostMapping(DAILY_ENTRIES)
    public DailyEntryDTO createDailyEntry(@RequestBody CreateDailyEntryDTO createDailyEntryDTO) {
        return dailyEntryAPI.createDailyEntryManually(createDailyEntryDTO);
    }

    @GetMapping(DAILY_ENTRIES)
    @SelfOnlyPermission(identifier = "employeeId")
    public DailyEntryDTO getDailyEntry(@RequestParam UUID employeeId, @RequestParam LocalDate entryDate) {
        return dailyEntryAPI.getByEmployeeIdAndEntryDate(employeeId, entryDate);
    }
}
