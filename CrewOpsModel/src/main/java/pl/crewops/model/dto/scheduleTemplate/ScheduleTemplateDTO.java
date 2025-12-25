package pl.crewops.model.dto.scheduleTemplate;

import java.util.List;
import java.util.UUID;
import lombok.Builder;
import pl.crewops.enums.ScheduleTemplateType;

@Builder
public record ScheduleTemplateDTO(
        UUID id,
        String name,
        ScheduleTemplateType type,
        UUID authorEmployeeId,
        boolean privateOwner,
        List<ScheduleTemplateDayDTO> days) {

    @Override
    public String toString() {
        return "ScheduleTemplateDTO{" + "id="
                + id + ", name='"
                + name + '\'' + ", type="
                + type + ", authorEmployeeId="
                + authorEmployeeId + ", privateOwner="
                + privateOwner + '}';
    }
}
