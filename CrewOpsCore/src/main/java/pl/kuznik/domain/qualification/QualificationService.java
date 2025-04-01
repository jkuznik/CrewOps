package pl.kuznik.domain.qualification;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import pl.kuznik.domain.qualification.dto.CreateQualificationDTO;
import pl.kuznik.entity.Qualification;

import static pl.kuznik.domain.qualification.QualificationMapper.mapToEntity;

@Service
@RequiredArgsConstructor
@Validated
class QualificationService {

    private final QualificationRepository qualificationRepository;

    public Qualification createQualification(@Valid @NotNull CreateQualificationDTO createQualificationDTO) {
        return qualificationRepository.save(mapToEntity(createQualificationDTO));
    }
}
