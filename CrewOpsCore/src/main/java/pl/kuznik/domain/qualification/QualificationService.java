package pl.kuznik.domain.qualification;

import static pl.kuznik.domain.qualification.QualificationMapper.mapToDTO;
import static pl.kuznik.domain.qualification.QualificationMapper.mapToEntity;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import pl.kuznik.domain.qualification.dto.CreateQualificationDTO;
import pl.kuznik.domain.qualification.dto.QualificationDTO;

@Service
@RequiredArgsConstructor
@Validated
class QualificationService {

    private final QualificationRepository qualificationRepository;

    public QualificationDTO createQualification(@Valid @NotNull CreateQualificationDTO createQualificationDTO) {
        return mapToDTO(qualificationRepository.save(mapToEntity(createQualificationDTO)));
    }
}
