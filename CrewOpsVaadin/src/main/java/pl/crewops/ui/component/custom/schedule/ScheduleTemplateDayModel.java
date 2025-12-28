package pl.crewops.ui.component.custom.schedule;

import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
class ScheduleTemplateDayModel {
    private final List<ShiftResource> shifts = new ArrayList<>();
    private int dayNumber;

    public ScheduleTemplateDayModel(int dayNumber) {
        this.dayNumber = dayNumber;
    }
}
