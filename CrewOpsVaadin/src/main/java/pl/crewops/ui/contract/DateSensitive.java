package pl.crewops.ui.contract;

import java.time.LocalDate;

public interface DateSensitive {

    void updateDependsOnDate(LocalDate date);
}
