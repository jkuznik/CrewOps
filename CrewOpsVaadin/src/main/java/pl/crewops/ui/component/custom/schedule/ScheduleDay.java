package pl.crewops.ui.component.custom.schedule;

import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
final class ScheduleDay {

    private int dayNumber;

    private final List<ShiftResource> shifts = new ArrayList<>();

    public ScheduleDay(int dayNumber) {
        this.dayNumber = dayNumber;
    }

    public void addShift(ShiftResource shift) {
        this.shifts.add(shift);
    }
}
