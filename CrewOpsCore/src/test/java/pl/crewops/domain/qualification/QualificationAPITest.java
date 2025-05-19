package pl.crewops.domain.qualification;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import pl.crewops.IntegrationTest;
import pl.crewops.model.Qualification;

@Transactional
class QualificationAPITest extends IntegrationTest {

    @Autowired
    private QualificationAPI qualificationAPI;

    @Test
    void shouldReturnQualification_whenQualificationExists() {
        // given
        var qualificationId = UUID.fromString("55555555-5555-5555-5555-555555555555");

        // when
        Qualification result = qualificationAPI.getQualification(qualificationId);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(qualificationId);
        assertThat(result.getDescription()).isEqualTo("Operator maszyn ciężkich – uprawnienia kat. I");
    }
}
