package pl.crewops.domain.jobPosition;

import static pl.crewops.domain.jobPosition.JobPositionMapper.mapToDTO;

import java.util.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.crewops.domain.machine.MachineAPI;
import pl.crewops.domain.qualification.QualificationAPI;
import pl.crewops.model.dto.jobPosition.CreateJobPositionDTO;
import pl.crewops.model.dto.jobPosition.JobPositionDTO;
import pl.crewops.model.dto.jobPosition.UpdateJobPositionDTO;
import pl.crewops.model.tenantSchema.JobPosition;
import pl.crewops.model.tenantSchema.Machine;
import pl.crewops.model.tenantSchema.Qualification;

@Slf4j
@Service
@RequiredArgsConstructor
class JobPositionService implements JobPositionAPI {

    private final JobPositionRepository jobPositionRepository;
    private final MachineAPI machineAPI;
    private final QualificationAPI qualificationAPI;

    @Override
    @Transactional
    public JobPositionDTO createJobPosition(CreateJobPositionDTO createJobPositionDTO) {
        JobPosition jobPosition = JobPositionMapper.mapToEntity(createJobPositionDTO);
        jobPosition = jobPositionRepository.save(jobPosition);

        if (createJobPositionDTO.machineDTO() != null) {
            Machine machine =
                    machineAPI.getMachine(createJobPositionDTO.machineDTO().id());
            jobPosition.setMachine(machine);
        }

        if (createJobPositionDTO.qualificationDTOS() != null) {
            Set<Qualification> qualifications = new HashSet<>();
            createJobPositionDTO.qualificationDTOS().forEach(qualificationDTO -> {
                Qualification qualification = qualificationAPI.getQualification(qualificationDTO.id());
                if (qualification != null) {
                    qualifications.add(qualification);
                }
            });
            jobPosition.setQualifications(qualifications);
        }

        return mapToDTO(jobPositionRepository.save(jobPosition));
    }

    @Override
    @Transactional
    public Optional<JobPosition> findById(UUID id) {
        return jobPositionRepository.findById(id);
    }

    @Override
    @Transactional
    public Optional<JobPosition> findByName(String name) {
        return jobPositionRepository.findByName(name);
    }

    @Override
    @Transactional
    public List<JobPositionDTO> getAllJobPositions() {
        List<JobPosition> all = jobPositionRepository.findAll();
        return all.stream().map(JobPositionMapper::mapToDTO).toList();
    }

    @Override
    @Transactional
    public JobPositionDTO updateJobPosition(UpdateJobPositionDTO updateJopPositionDTO) {
        JobPosition jobPosition = jobPositionRepository
                .findById(updateJopPositionDTO.id())
                .
                // todo : custom exception
                orElseThrow(() -> new NoSuchElementException());

        if (updateJopPositionDTO.name() != null) {
            jobPosition.setName(updateJopPositionDTO.name());
        }
        if (updateJopPositionDTO.machineDTO() != null) {
            jobPosition.setMachine(
                    machineAPI.getMachine(updateJopPositionDTO.machineDTO().id()));
        }
        if (updateJopPositionDTO.qualifications() != null) {
            Set<Qualification> qualifications = new HashSet<>();
            updateJopPositionDTO.qualifications().forEach(qualificationDTO -> {
                qualifications.add(qualificationAPI.getQualification(qualificationDTO.id()));
                jobPosition.setQualifications(qualifications);
            });
        }

        return mapToDTO(jobPositionRepository.save(jobPosition));
    }

    @Override
    public void deleteById(UUID id) {
        jobPositionRepository.deleteById(id);

        log.info(jobPositionRepository.findById(id).toString());
    }
}
