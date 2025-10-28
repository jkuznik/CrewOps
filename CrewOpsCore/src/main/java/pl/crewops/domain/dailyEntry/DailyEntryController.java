package pl.crewops.domain.dailyEntry;

import static pl.crewops.enums.ControllerURL.DAILY_ENTRIES;
import static pl.crewops.enums.ControllerURL.DAILY_ENTRIES_APPROVE;

import java.time.LocalDate;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import pl.crewops.model.dto.dailyEntry.CreateDailyEntryDTO;
import pl.crewops.model.dto.dailyEntry.DailyEntryDTO;
import pl.crewops.model.dto.dailyEntry.UpdateDailyEntryCommand;
import pl.crewops.security.custom.permissionAnnotation.SelfOnlyPermission;
import pl.crewops.security.custom.permissionAnnotation.ShiftLeaderPermission;

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

    // consider second authentication on this method for self modifications
    @PatchMapping(DAILY_ENTRIES)
    @SelfOnlyPermission(identifier = "actionByEmployeeId")
    public DailyEntryDTO updateDailyEntry(@RequestBody UpdateDailyEntryCommand updateDailyEntryCommand) {
        return dailyEntryAPI.updateDailyEntry(updateDailyEntryCommand);
    }

    @PatchMapping(DAILY_ENTRIES_APPROVE)
    @ShiftLeaderPermission
    public DailyEntryDTO approveDailyEntry(@RequestBody UpdateDailyEntryCommand updateDailyEntryCommand) {
        return dailyEntryAPI.approveDailyEntry(updateDailyEntryCommand);
    }
}
