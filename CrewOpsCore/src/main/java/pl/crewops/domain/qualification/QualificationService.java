package pl.crewops.domain.qualification;

import static pl.crewops.domain.qualification.QualificationMapper.mapToDTO;
import static pl.crewops.domain.qualification.QualificationMapper.mapToEntity;
import static pl.crewops.utils.pagination.PageRequestFactory.createPageRequest;

import java.util.List;
import java.util.Set;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.crewops.dto.qualification.CreateQualificationDTO;
import pl.crewops.dto.qualification.QualificationDTO;
import pl.crewops.dto.qualification.UpdateQualificationDTO;
import pl.crewops.exception.QualificationNotFoundException;
import pl.crewops.model.Qualification;

@Slf4j
@Service
@RequiredArgsConstructor
class QualificationService implements QualificationAPI {

    private final QualificationRepository qualificationRepository;

    public QualificationDTO createQualification(CreateQualificationDTO createQualificationDTO) {
        log.info("Create qualification: {}", createQualificationDTO);
        return mapToDTO(qualificationRepository.save(mapToEntity(createQualificationDTO)));
    }

    public Qualification getQualification(UUID qualificationId) {
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
    public QualificationDTO updateQualification(UpdateQualificationDTO updateQualificationDTO) {
        Qualification qualification = getQualification(updateQualificationDTO.qualificationId());

        qualification.setDescription(updateQualificationDTO.description());

        log.info("Update qualification: {}", qualification);
        return mapToDTO(qualification);
    }

    @Transactional
    public void deleteQualification(UUID qualificationId) {
        log.info("Delete qualification: {}", qualificationId);
        qualificationRepository.deleteById(qualificationId);
    }
}
