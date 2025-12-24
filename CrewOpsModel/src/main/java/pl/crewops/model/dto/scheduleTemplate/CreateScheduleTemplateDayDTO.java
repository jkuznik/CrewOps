package pl.crewops.model.dto.scheduleTemplate;

import java.util.List;
import lombok.Builder;

@Builder
public record CreateScheduleTemplateDayDTO(int dayIndex, List<CreateScheduleTemplateItemDTO> items) {}
