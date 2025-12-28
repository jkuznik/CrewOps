package pl.crewops.domain.scheduleTemplate;

import java.util.List;
import org.springframework.validation.annotation.Validated;
import pl.crewops.model.dto.scheduleTemplate.CreateScheduleTemplateDTO;
import pl.crewops.model.dto.scheduleTemplate.ScheduleTemplateDTO;

@Validated
public interface ScheduleAPI {

    ScheduleTemplateDTO createTemplate(CreateScheduleTemplateDTO createScheduleTemplateDTO);

    List<ScheduleTemplateDTO> getAllTemplates();

    List<ScheduleTemplateDTO> findAll();
}
