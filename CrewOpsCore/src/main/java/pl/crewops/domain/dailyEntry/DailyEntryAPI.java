package pl.crewops.domain.dailyEntry;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.UUID;
import org.springframework.validation.annotation.Validated;
import pl.crewops.model.dto.dailyEntry.CreateDailyEntryDTO;
import pl.crewops.model.dto.dailyEntry.DailyEntryDTO;

@Validated
public interface DailyEntryAPI {

    DailyEntryDTO createDailyEntry(@NotNull @Valid CreateDailyEntryDTO createDailyEntryDTO);

    DailyEntryDTO getByEmployeeIdAndEntryDate(@NotNull UUID employeeId, @NotNull LocalDate entryDate);
}
