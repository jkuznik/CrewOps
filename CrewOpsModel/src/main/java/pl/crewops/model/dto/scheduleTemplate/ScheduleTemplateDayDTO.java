package pl.crewops.model.dto.scheduleTemplate;

import java.util.List;
import java.util.UUID;
import lombok.Builder;

@Builder
public record ScheduleTemplateDayDTO(UUID id, int dayIndex, List<ScheduleTemplateItemDTO> items) {}
