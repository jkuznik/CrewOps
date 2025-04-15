package pl.crewops.domain.qualification;

import static pl.crewops.domain.qualification.QualificationMapper.mapToDTO;
import static pl.crewops.domain.qualification.QualificationMapper.mapToEntity;
import static pl.crewops.utils.pagination.PageRequestFactory.createPageRequest;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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

@Slf4j
@Service
@RequiredArgsConstructor
@Validated
class QualificationService {

    private final QualificationRepository qualificationRepository;

    public QualificationDTO createQualification(@Valid @NotNull CreateQualificationDTO createQualificationDTO) {
        log.info("Create qualification: {}", createQualificationDTO);
        return mapToDTO(qualificationRepository.save(mapToEntity(createQualificationDTO)));
    }

    public Qualification getQualification(@NotNull UUID qualificationId) {
        log.info("Get qualification: {}", qualificationId);
        return qualificationRepository
                .findById(qualificationId)
                .orElseThrow(() -> new QualificationNotFoundException(qualificationId));
    }

    public List<QualificationDTO> getQualificationsIn(Set<UUID> ids) {
        log.info("Get qualifications in: {}", ids);
        return qualificationRepository.findAllByIdIn(ids).stream()
                .map(QualificationMapper::mapToDTO)
                .toList();
    }

    public List<QualificationDTO> getAllQualifications(int page, int size) {
        PageRequest pageRequest = createPageRequest(page, size, Sort.by(Sort.Order.asc("description")));

        log.info("Get all qualifications with pagination. Page: {}, size: {}", page, size);
        return qualificationRepository.findAll(pageRequest).stream()
                .map(QualificationMapper::mapToDTO)
                .toList();
    }

    @Transactional
    public QualificationDTO updateQualification(@Valid @NotNull UpdateQualificationDTO updateQualificationDTO) {
        Qualification qualification = getQualification(updateQualificationDTO.qualificationId());

        qualification.setDescription(updateQualificationDTO.description());

        log.info("Update qualification: {}", qualification);
        return mapToDTO(qualification);
    }

    @Transactional
    public void deleteQualification(@NotNull UUID qualificationId) {
        log.info("Delete qualification: {}", qualificationId);
        qualificationRepository.deleteById(qualificationId);
    }
}
