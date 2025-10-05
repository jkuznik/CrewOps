package pl.crewops.domain.qualification;

import static pl.crewops.domain.qualification.QualificationMapper.mapToDTO;
import static pl.crewops.domain.qualification.QualificationMapper.mapToEntity;
import static pl.crewops.util.pagination.PageRequestFactory.createPageRequest;

import java.util.List;
import java.util.Set;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.crewops.exception.domain.qualification.QualificationNotFoundException;
import pl.crewops.model.dto.qualification.CreateQualificationDTO;
import pl.crewops.model.dto.qualification.QualificationDTO;
import pl.crewops.model.dto.qualification.UpdateQualificationDTO;
import pl.crewops.model.tenantSchema.Qualification;

@Slf4j
@Service
@RequiredArgsConstructor
class QualificationService implements QualificationAPI {

    private final QualificationRepository qualificationRepository;

    @Transactional
    public QualificationDTO createQualification(CreateQualificationDTO createQualificationDTO) {
        log.info("Create qualification: {}", createQualificationDTO);
        return mapToDTO(qualificationRepository.save(mapToEntity(createQualificationDTO)));
    }

    @Transactional(readOnly = true)
    public Qualification getQualification(UUID qualificationId) {
        log.info("Get qualification: {}", qualificationId);
        return qualificationRepository
                .findById(qualificationId)
                .orElseThrow(() -> new QualificationNotFoundException(qualificationId));
    }

    @Transactional(readOnly = true)
    public List<QualificationDTO> getQualificationsIn(Set<UUID> ids) {
        log.info("Get qualifications in: {}", ids);
        return qualificationRepository.findAllByIdIn(ids).stream()
                .map(QualificationMapper::mapToDTO)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<QualificationDTO> getAllQualifications(int page, int size) {
        PageRequest pageRequest = createPageRequest(page, size, Sort.by(Sort.Order.asc("description")));

        log.info("Get all qualifications with pagination. Page: {}, size: {}", page, size);
        return qualificationRepository.findAll(pageRequest).stream()
                .map(QualificationMapper::mapToDTO)
                .toList();
    }

    @Override
    public List<QualificationDTO> getAllQualificationsWithExpirationTimeByEmployeeId(UUID employeeId) {
        return qualificationRepository.findAllQualificationsWithExpiredAtByEmployeeId(employeeId).stream()
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
