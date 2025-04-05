package pl.crewops.domain.qualification;

import static pl.crewops.domain.qualification.QualificationMapper.mapToDTO;
import static pl.crewops.domain.qualification.QualificationMapper.mapToEntity;
import static pl.crewops.utils.pagination.PageRequestFactory.createPageRequest;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import pl.crewops.dto.qualification.CreateQualificationDTO;
import pl.crewops.dto.qualification.QualificationDTO;
import pl.crewops.dto.qualification.UpdateQualificationDTO;
import pl.crewops.exception.QualificationNotFoundException;
import pl.crewops.model.Qualification;

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

    public List<QualificationDTO> getAllQualifications(int page, int size) {
        PageRequest pageRequest = createPageRequest(page, size, Sort.by(Sort.Order.asc("description")));

        return qualificationRepository.findAll(pageRequest).stream()
                .map(QualificationMapper::mapToDTO)
                .toList();
    }

    @Transactional
    public QualificationDTO updateQualification(@Valid @NotNull UpdateQualificationDTO updateQualificationDTO) {
        Qualification qualification = getQualification(updateQualificationDTO.qualificationId());

        qualification.setDescription(updateQualificationDTO.description());

        return mapToDTO(qualification);
    }

    @Transactional
    public void deleteQualification(@NotNull UUID qualificationId) {
        qualificationRepository.deleteById(qualificationId);
    }
}
