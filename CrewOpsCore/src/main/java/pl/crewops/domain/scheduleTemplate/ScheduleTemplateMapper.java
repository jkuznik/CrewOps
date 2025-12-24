package pl.crewops.domain.scheduleTemplate;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import pl.crewops.domain.shift.ShiftMapper;
import pl.crewops.model.dto.scheduleTemplate.*;
import pl.crewops.model.tenantSchema.ScheduleTemplate;
import pl.crewops.model.tenantSchema.ScheduleTemplateDay;
import pl.crewops.model.tenantSchema.ScheduleTemplateItem;

@Mapper(
        componentModel = "spring",
        uses = {ShiftMapper.class})
public interface ScheduleTemplateMapper {

    // --- TEMPLATE ---

    @Mapping(target = "authorEmployeeId", source = "author.id")
    ScheduleTemplateDTO toDto(ScheduleTemplate entity);

    @Mapping(target = "author", ignore = true)
    ScheduleTemplate toEntity(CreateScheduleTemplateDTO dto);

    // --- DAY ---

    ScheduleTemplateDayDTO toDto(ScheduleTemplateDay entity);

    @Mapping(target = "scheduleTemplate", ignore = true)
    ScheduleTemplateDay toEntity(CreateScheduleTemplateDayDTO dto);

    // --- ITEM ---

    ScheduleTemplateItemDTO toDto(ScheduleTemplateItem entity);

    @Mapping(target = "shift", ignore = true)
    @Mapping(target = "scheduleTemplateDay", ignore = true)
    ScheduleTemplateItem toEntity(CreateScheduleTemplateItemDTO dto);
}
