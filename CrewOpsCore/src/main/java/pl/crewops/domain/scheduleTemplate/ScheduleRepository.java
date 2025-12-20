package pl.crewops.domain.scheduleTemplate;

import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import pl.crewops.model.tenantSchema.ScheduleTemplate;
import pl.crewops.model.tenantSchema.ScheduleTemplateDay;
import pl.crewops.model.tenantSchema.ScheduleTemplateItem;

interface ScheduleTemplateRepository extends JpaRepository<ScheduleTemplate, UUID> {}

interface ScheduleTemplateDayRepository extends JpaRepository<ScheduleTemplateDay, UUID> {}

interface ScheduleTemplateItemRepository extends JpaRepository<ScheduleTemplateItem, UUID> {}
