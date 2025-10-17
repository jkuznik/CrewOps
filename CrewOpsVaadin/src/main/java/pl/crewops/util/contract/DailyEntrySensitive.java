package pl.crewops.util.contract;

import java.time.LocalDate;

public interface DailyEntrySensitive {

    void updateDependsOnDate(LocalDate date);
}
