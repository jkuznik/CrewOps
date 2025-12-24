package pl.crewops.model.dto.scheduleTemplate;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import pl.crewops.model.dto.shift.ShiftDTO;

@Builder
public record CreateScheduleTemplateItemDTO(@NotNull ShiftDTO shift, int startMinute, int durationMinutes) {}
