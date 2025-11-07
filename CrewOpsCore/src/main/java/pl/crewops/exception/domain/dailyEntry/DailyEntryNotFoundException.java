package pl.crewops.exception.domain.dailyEntry;

import java.time.LocalDate;
import java.util.UUID;

public class DailyEntryNotFoundException extends RuntimeException {
    public DailyEntryNotFoundException(UUID employee, LocalDate entryDate) {
        super("Employee with id: " + employee + " has not registered entry  for date: " + entryDate);
    }

    public DailyEntryNotFoundException(UUID uuid) {
        super("Daily entry with id: " + uuid + " not found");
    }
}
