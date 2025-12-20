package pl.crewops.model.dto.scheduleTemplate;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.List;
import java.util.UUID;
import lombok.Builder;
import pl.crewops.enums.ScheduleTemplateType;

@Builder
public record CreateScheduleTemplateDTO(
        @NotNull @Size(max = 63) String name,
        @NotNull ScheduleTemplateType type,
        @NotNull UUID authorEmployeeId,
        @NotNull boolean isPrivate,
        List<ScheduleTemplateDayDTO> days) {}
