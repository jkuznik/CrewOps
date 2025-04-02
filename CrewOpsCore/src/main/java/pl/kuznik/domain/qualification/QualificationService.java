package pl.kuznik.domain.qualification;

import static pl.kuznik.domain.qualification.QualificationMapper.mapToDTO;
import static pl.kuznik.domain.qualification.QualificationMapper.mapToEntity;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import pl.kuznik.domain.qualification.dto.CreateQualificationDTO;
import pl.kuznik.domain.qualification.dto.QualificationDTO;
import pl.kuznik.entity.Qualification;
import pl.kuznik.exception.QualificationNotFoundException;

@Service
@RequiredArgsConstructor
@Validated
class QualificationService {

    private final QualificationRepository qualificationRepository;

    public QualificationDTO createQualification(@Valid @NotNull CreateQualificationDTO createQualificationDTO) {
        return mapToDTO(qualificationRepository.save(mapToEntity(createQualificationDTO)));
    }

    public Qualification getQualification(@NotNull UUID qualificationId) {
        return qualificationRepository
                .findById(qualificationId)
                .orElseThrow(() -> new QualificationNotFoundException(qualificationId));
    }
}
