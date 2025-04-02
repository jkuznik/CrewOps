package pl.kuznik.domain.qualification;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import org.springframework.validation.beanvalidation.MethodValidationPostProcessor;

@SpringJUnitConfig(classes = {QualificationService.class, MethodValidationPostProcessor.class})
class QualificationServiceTest {

    @MockitoBean
    private QualificationRepository qualificationRepository;

    @Autowired
    private QualificationService qualificationService;

    @Test
    void shouldReturnQualificationDTO_whenCreatedQualificationDTOHaveNoEmployees() {}
}
