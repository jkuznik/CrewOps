package pl.crewops.domain.scheduleTemplate;

import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.crewops.domain.employee.EmployeeAPI;
import pl.crewops.domain.shift.ShiftAPI;
import pl.crewops.model.dto.scheduleTemplate.CreateScheduleTemplateDTO;
import pl.crewops.model.dto.scheduleTemplate.ScheduleTemplateDTO;
import pl.crewops.model.tenantSchema.*;

@Service
@RequiredArgsConstructor
public class ScheduleService implements ScheduleAPI {

    private final ScheduleTemplateRepository templateRepository;
    private final ScheduleTemplateDayRepository dayRepository;
    private final ScheduleTemplateItemRepository itemRepository;
    private final ScheduleTemplateMapper mapper;

    private final EmployeeAPI employeeAPI;
    private final ShiftAPI shiftAPI;

    @Override
    @Transactional
    public ScheduleTemplateDTO createTemplate(CreateScheduleTemplateDTO createScheduleTemplateDTO) {

        Employee employeeById = employeeAPI.getEmployeeById(createScheduleTemplateDTO.authorEmployeeId());

        ScheduleTemplate scheduleTemplate = mapper.toEntity(createScheduleTemplateDTO);
        scheduleTemplate.setAuthor(employeeById);

        List<ScheduleTemplateDay> days = new ArrayList<>();
        createScheduleTemplateDTO.days().forEach(day -> {
            ScheduleTemplateDay scheduleTemplateDay = mapper.toEntity(day);

            List<ScheduleTemplateItem> items = new ArrayList<>();
            day.items().forEach(item -> {
                Shift shiftById = shiftAPI.getShiftById(item.shift().id());

                ScheduleTemplateItem scheduleTemplateItem = mapper.toEntity(item);
                scheduleTemplateItem.setShift(shiftById);
                scheduleTemplateItem.setScheduleTemplateDay(scheduleTemplateDay);
                items.add(scheduleTemplateItem);
            });

            scheduleTemplateDay.setScheduleTemplate(scheduleTemplate);
            scheduleTemplateDay.setItems(items);
            days.add(scheduleTemplateDay);
        });

        scheduleTemplate.setDays(days);

        // todo zrobic  CREATEscheduletemplate DAY DTO i dodać logike tworzenia takich rekordów

        return mapper.toDto(templateRepository.save(scheduleTemplate));
    }
}
