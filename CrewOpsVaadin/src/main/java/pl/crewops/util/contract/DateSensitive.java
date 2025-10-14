package pl.crewops.util.contract;

import java.time.LocalDate;

public interface DateSensitive {

    void updateDependsOnDate(LocalDate date);
}
