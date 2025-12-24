package pl.crewops.domain.scheduleTemplate;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import pl.crewops.IntegrationTest;
import pl.crewops.enums.ScheduleTemplateType;
import pl.crewops.model.dto.scheduleTemplate.*;
import pl.crewops.model.dto.shift.ShiftDTO;
import pl.crewops.model.tenantSchema.ScheduleTemplateDay;

@Transactional
public class ScheduleAPITest extends IntegrationTest {

    @Autowired
    ScheduleTemplateDayRepository scheduleTemplateDayRepository;

    @Test
    void createTemplate_shouldReturnScheduleTemplateDTO_inSuccessCase() {
        // given

        var shiftDTO = ShiftDTO.builder()
                .id(UUID.fromString("55550000-aaaa-1111-aaaa-000000000001"))
                .build();

        var scheduleTemplateItemDTO = CreateScheduleTemplateItemDTO.builder()
                .shift(shiftDTO)
                .startMinute(0)
                .durationMinutes(440)
                .build();

        var scheduleTemplateDayDTO = CreateScheduleTemplateDayDTO.builder()
                .dayIndex(1)
                .items(List.of(scheduleTemplateItemDTO))
                .build();

        var createScheduleTemplateDTO = CreateScheduleTemplateDTO.builder()
                .name("foo")
                .authorEmployeeId(UUID.fromString("11111111-1111-1111-1111-111111111111"))
                .type(ScheduleTemplateType.DAILY)
                .days(List.of(scheduleTemplateDayDTO))
                .privateOwner(false)
                .build();

        // when & then

        ScheduleTemplateDTO result = scheduleAPI.createTemplate(createScheduleTemplateDTO);

        List<ScheduleTemplateDay> all = scheduleTemplateDayRepository.findAll();
        all.forEach(System.out::println);

        assertThat(result).isNotNull();
        assertThat(result.id()).isNotNull();
        assertThat(result.name()).isEqualTo("foo");
        assertThat(result.type()).isEqualTo(ScheduleTemplateType.DAILY);
        assertThat(result.authorEmployeeId()).isEqualTo(UUID.fromString("11111111-1111-1111-1111-111111111111"));
        assertThat(result.privateOwner()).isFalse();
        assertThat(result.days().size()).isEqualTo(1);

        assertThat(result.days().getFirst().dayIndex()).isEqualTo(1);
        assertThat(result.days().getFirst().items().getFirst().startMinute()).isEqualTo(0);
        assertThat(result.days().getFirst().items().getFirst().durationMinutes())
                .isEqualTo(440);
    }
}
