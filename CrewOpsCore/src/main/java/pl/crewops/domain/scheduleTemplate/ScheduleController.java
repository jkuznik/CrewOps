package pl.crewops.domain.scheduleTemplate;

import static pl.crewops.enums.ControllerURL.SCHEDULE;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import pl.crewops.model.dto.scheduleTemplate.CreateScheduleTemplateDTO;
import pl.crewops.model.dto.scheduleTemplate.ScheduleTemplateDTO;

@RestController
@RequiredArgsConstructor
class ScheduleController {

    private final ScheduleAPI scheduleAPI;

    @PostMapping(SCHEDULE)
    public ResponseEntity<ScheduleTemplateDTO> createSchedule(
            @RequestBody CreateScheduleTemplateDTO createScheduleTemplateDTO) {
        return ResponseEntity.ok(scheduleAPI.createTemplate(createScheduleTemplateDTO));
    }

    @GetMapping(SCHEDULE)
    public ResponseEntity<List<ScheduleTemplateDTO>> getAllTemplates() {
        return ResponseEntity.ok(scheduleAPI.getAllTemplates());
    }
}
