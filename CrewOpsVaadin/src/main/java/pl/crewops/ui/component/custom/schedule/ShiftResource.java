package pl.crewops.ui.component.custom.schedule;

import java.util.Objects;
import java.util.UUID;
import lombok.Getter;
import lombok.Setter;
import pl.crewops.model.dto.shift.ShiftDTO;

@Getter
@Setter
public final class ShiftResource {
    private ShiftDTO shiftDTO;
    private String instanceId = UUID.randomUUID().toString();

    private int startMinute; // 0 - 1439
    private int durationMinutes = 480; // Default 8h (480 min)

    private int beforeMoveStartMinute;
    private boolean hasCrossMidnightSegment = false;
    private boolean isCrossMidnightSegment = false;

    public ShiftResource(ShiftDTO shiftDTO) {
        this.shiftDTO = shiftDTO;
    }

    public int getEndMinute() {
        return startMinute + durationMinutes;
    }

    public int getNextDayEndMinute() {
        return getEndMinute() - 1440;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ShiftResource that)) return false;
        return Objects.equals(instanceId, that.instanceId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(instanceId);
    }
}
