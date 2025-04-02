package pl.kuznik.domain.qualification;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchException;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static pl.kuznik.domain.qualification.QualificationTestFactory.createQualification;

import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import org.springframework.validation.beanvalidation.MethodValidationPostProcessor;
import pl.kuznik.domain.qualification.dto.CreateQualificationDTO;
import pl.kuznik.domain.qualification.dto.QualificationDTO;
import pl.kuznik.entity.Qualification;

@SpringJUnitConfig(classes = {QualificationService.class, MethodValidationPostProcessor.class})
class QualificationServiceTest {

    @MockitoBean
    private QualificationRepository qualificationRepository;

    @Autowired
    private QualificationService qualificationService;

    private Qualification qualification = createQualification();
    private CreateQualificationDTO createQualificationDTO =
            QualificationTestFactory.createCreateQualificationDTOWithDescription();
    private CreateQualificationDTO createQualificationDTONotValid =
            QualificationTestFactory.createCreateQualificationDTOWithoutDescription();

    @Test
    void shouldReturnQualificationDTO_whenCreatedQualificationDTOIsValid() {
        // when
        when(qualificationRepository.save(any(Qualification.class))).thenReturn(qualification);
        QualificationDTO result = qualificationService.createQualification(createQualificationDTO);

        // then
        assertThat(result).isNotNull();
        assertThat(result.description()).isEqualTo("foo1");
    }

    @Test
    void shouldThrowException_whenCreatedQualificationDTOIsNotValid() {
        // when
        Exception result =
                catchException(() -> qualificationService.createQualification(createQualificationDTONotValid));

        // then
        assertThat(result).isInstanceOf(ConstraintViolationException.class);
    }

    @Test
    void shouldThrowException_whenCreatedQualificationDTOIsNull() {
        // when
        Exception result = catchException(() -> qualificationService.createQualification(null));

        // then
        assertThat(result).isInstanceOf(ConstraintViolationException.class);
    }
}
