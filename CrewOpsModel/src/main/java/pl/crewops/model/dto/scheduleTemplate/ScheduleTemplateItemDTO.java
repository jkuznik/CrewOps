package pl.crewops.model.dto.scheduleTemplate;

import java.util.UUID;
import lombok.Builder;
import pl.crewops.model.dto.shift.ShiftDTO;

@Builder
public record ScheduleTemplateItemDTO(UUID id, ShiftDTO shift, int startMinute, int durationMinutes) {}
