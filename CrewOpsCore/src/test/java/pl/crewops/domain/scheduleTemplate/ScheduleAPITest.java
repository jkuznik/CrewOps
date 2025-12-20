package pl.crewops.domain.scheduleTemplate;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.transaction.annotation.Transactional;
import pl.crewops.IntegrationTest;
import pl.crewops.enums.ScheduleTemplateType;
import pl.crewops.model.dto.scheduleTemplate.CreateScheduleTemplateDTO;
import pl.crewops.model.dto.scheduleTemplate.ScheduleTemplateDTO;

@Transactional
public class ScheduleAPITest extends IntegrationTest {

    @Test
    void createTemplate_shouldReturnScheduleTemplateDTO_inSuccessCase() {
        // given
        var createScheduleTemplateDTO = CreateScheduleTemplateDTO.builder()
                .name("foo")
                // existed employee id in test db
                .authorEmployeeId(UUID.fromString("11111111-1111-1111-1111-111111111111"))
                .type(ScheduleTemplateType.DAILY)
                .days(List.of())
                .isPrivate(false)
                .build();

        // when & then

        ScheduleTemplateDTO result = scheduleAPI.createTemplate(createScheduleTemplateDTO);

        assertThat(result).isNotNull();
        assertThat(result.id()).isNotNull();
        assertThat(result.name()).isEqualTo("foo");
    }
}
