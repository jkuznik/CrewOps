package pl.crewops.domain.qualification;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchException;
import static pl.crewops.domain.qualification.QualificationTestFactory.createQualificationDTOWithoutDescription;
import static pl.crewops.domain.qualification.QualificationTestFactory.updateQualificationDTOWithoutDescription;

import jakarta.validation.ConstraintViolationException;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.transaction.annotation.Transactional;
import pl.crewops.IntegrationTest;
import pl.crewops.model.Qualification;

@Transactional
class QualificationAPITest extends IntegrationTest {

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

    @Test
    void shouldThrowException_whenCreatedQualificationDTOIsNotValid() {
        // given
        var createQualificationDTOWithoutDescription = createQualificationDTOWithoutDescription();

        // when
        Exception result =
                catchException(() -> qualificationAPI.createQualification(createQualificationDTOWithoutDescription));

        // then
        assertThat(result).isInstanceOf(ConstraintViolationException.class);
    }

    @Test
    void shouldThrowException_whenCreatedQualificationDTOIsNull() {
        // when
        Exception result = catchException(() -> qualificationAPI.createQualification(null));

        // then
        assertThat(result).isInstanceOf(ConstraintViolationException.class);
    }

    @Test
    void shouldThrowException_whenUpdatedQualificationDTOIsNotValid() {
        // given
        var updateQualificationDTOWithoutDescription = updateQualificationDTOWithoutDescription();

        // when
        Exception result =
                catchException(() -> qualificationAPI.updateQualification(updateQualificationDTOWithoutDescription));

        // then
        assertThat(result).isInstanceOf(ConstraintViolationException.class);
    }
}
