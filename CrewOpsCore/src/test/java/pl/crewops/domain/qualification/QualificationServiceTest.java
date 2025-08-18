package pl.crewops.domain.qualification;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static pl.crewops.domain.qualification.QualificationTestFactory.*;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import pl.crewops.dto.qualification.CreateQualificationDTO;
import pl.crewops.dto.qualification.QualificationDTO;
import pl.crewops.dto.qualification.UpdateQualificationDTO;
import pl.crewops.model.Qualification;

@SpringJUnitConfig(classes = {QualificationService.class})
class QualificationServiceTest {

    @MockitoBean
    private QualificationRepository qualificationRepository;

    @Autowired
    private QualificationService qualificationService;

    private Qualification qualification;
    private CreateQualificationDTO createQualificationDTO;
    private UpdateQualificationDTO updateQualificationDTOWithDescription;
    private final UUID qualificationId = UUID.randomUUID();

    @BeforeEach
    void setUp() {
        qualification = qualification();
        createQualificationDTO = createQualificationDTOWithDescription();
        updateQualificationDTOWithDescription = updateQualificationDTOWithDescription();
    }

    @Test
    void shouldReturnQualificationDTO_whenCreatedQualificationDTOIsValid() {
        // when
        when(qualificationRepository.save(any(Qualification.class))).thenReturn(qualification);
        QualificationDTO result = qualificationService.createQualification(createQualificationDTO);

        // then
        assertThat(result).isNotNull();
        assertThat(result.description()).isEqualTo("description");
    }

    @Test
    void shouldReturnQualificationDTOsList_whenQualificationsExist() {
        // given
        Page<Qualification> qualifications = new PageImpl<>(Collections.singletonList(qualification));

        // when
        when(qualificationRepository.findAll(any(PageRequest.class))).thenReturn(qualifications);
        List<QualificationDTO> result = qualificationService.getAllQualifications(0, 5);

        // then
        assertThat(result).isNotNull();
        assertThat(result.size()).isEqualTo(1);
        assertThat(result.get(0).description()).isEqualTo("description");
    }

    @Test
    void shouldReturnQualificationDTO_whenUpdatedQualificationDTOIsValid() {
        // when
        when(qualificationRepository.findById(any(UUID.class))).thenReturn(Optional.of(qualification));
        QualificationDTO result = qualificationService.updateQualification(updateQualificationDTOWithDescription);

        // then
        assertThat(result).isNotNull();
        assertThat(result.description()).isEqualTo(qualification.getDescription());
    }

    @Test
    void shouldDeleteQualification() {
        // when
        doNothing().when(qualificationRepository).deleteById(any(UUID.class));
        qualificationService.deleteQualification(qualificationId);

        // then
        verify(qualificationRepository, times(1)).deleteById(qualificationId);
    }
}
