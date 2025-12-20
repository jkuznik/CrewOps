package pl.crewops.domain.scheduleTemplate;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pl.crewops.domain.employee.EmployeeAPI;
import pl.crewops.model.dto.scheduleTemplate.CreateScheduleTemplateDTO;
import pl.crewops.model.dto.scheduleTemplate.ScheduleTemplateDTO;
import pl.crewops.model.tenantSchema.Employee;
import pl.crewops.model.tenantSchema.ScheduleTemplate;

@Service
@RequiredArgsConstructor
public class ScheduleService implements ScheduleAPI {

    private final ScheduleTemplateRepository templateRepository;
    private final ScheduleTemplateDayRepository dayRepository;
    private final ScheduleTemplateItemRepository itemRepository;
    private final ScheduleTemplateMapper mapper;

    private final EmployeeAPI employeeAPI;

    @Override
    public ScheduleTemplateDTO createTemplate(CreateScheduleTemplateDTO createScheduleTemplateDTO) {

        Employee employeeById = employeeAPI.getEmployeeById(createScheduleTemplateDTO.authorEmployeeId());

        ScheduleTemplate scheduleTemplate = mapper.toEntity(createScheduleTemplateDTO);
        scheduleTemplate.setAuthor(employeeById);

        return mapper.toDto(templateRepository.save(scheduleTemplate));
    }
}
