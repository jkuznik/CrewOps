package pl.crewops.ui.component.custom.schedule;

import lombok.Getter;
import lombok.Setter;
import pl.crewops.enums.TimeSlot;
import pl.crewops.model.dto.shift.ShiftDTO;

@Getter
@Setter
final class ShiftResource {
    private final ShiftDTO shiftDTO;
    private TimeSlot startSlot;
    private int durationInSlots = 1;

    public ShiftResource(ShiftDTO shiftDTO) {
        this.shiftDTO = shiftDTO;
    }

    public int getStartSlotIndex() {
        return startSlot.getIndex();
    }

    public int getEndSlotIndex() {
        return startSlot.getIndex() + durationInSlots;
    }

    public int getNextDayEndSlotForShift() {
        int endSlotIndex = getEndSlotIndex();
        return endSlotIndex % 96;
    }
}
