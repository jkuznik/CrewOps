package pl.crewops.ui.component.custom.schedule;

import java.util.ArrayList;
import java.util.List;
import lombok.Getter;

@Getter
final class ScheduleDay {

    private final List<ShiftResource> shifts = new ArrayList<>();

    private final int dayNumber;

    public ScheduleDay(int dayNumber) {
        this.dayNumber = dayNumber;
    }

    public void addShift(ShiftResource shift) {
        this.shifts.add(shift);
    }
}
