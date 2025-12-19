package pl.crewops.ui.component.custom.schedule;

import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ScheduleDay {
    private final List<ShiftResource> shifts = new ArrayList<>();
    private int dayNumber;

    public ScheduleDay(int dayNumber) {
        this.dayNumber = dayNumber;
    }
}
