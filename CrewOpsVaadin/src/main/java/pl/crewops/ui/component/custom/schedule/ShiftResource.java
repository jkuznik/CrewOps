package pl.crewops.ui.component.custom.schedule;

import static pl.crewops.ui.component.custom.schedule.DailyScheduleGrid.INTERVALS_PER_DAY;

import java.util.Objects;
import lombok.Getter;
import lombok.Setter;
import pl.crewops.enums.TimeSlot;
import pl.crewops.model.dto.shift.ShiftDTO;

@Getter
@Setter
final class ShiftResource {
    private final ShiftDTO shiftDTO;
    private TimeSlot startSlot;
    private int durationInSlots = 84;

    private int beforeMoveStartSlot;
    private boolean hasCrossMidnightSegment = false; // this is info for original shift resource
    private boolean isCrossMidnightSegment = false; // this is info for 'next day' visualisation shift

    public ShiftResource(ShiftDTO shiftDTO) {
        this.shiftDTO = shiftDTO;
    }

    public boolean hasCrossMidnightSegment() {
        return hasCrossMidnightSegment;
    }

    public int getStartSlotIndex() {
        return startSlot.getIndex();
    }

    public int getEndSlotIndex() {
        return startSlot.getIndex() + durationInSlots;
    }

    public int getNextDayEndSlotForShift() {
        int endSlotIndex = getEndSlotIndex();
        return endSlotIndex - INTERVALS_PER_DAY;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof ShiftResource that)) return false;
        return Objects.equals(getShiftDTO(), that.getShiftDTO());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getShiftDTO());
    }
}
