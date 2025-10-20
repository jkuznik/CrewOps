package pl.crewops.domain.jobPosition;

import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import pl.crewops.domain.machine.MachineAPI;
import pl.crewops.model.dto.jobPosition.CreateJobPositionDTO;
import pl.crewops.model.dto.jobPosition.JobPositionDTO;
import pl.crewops.model.tenantSchema.JobPosition;
import pl.crewops.model.tenantSchema.Machine;

@Slf4j
@Service
@RequiredArgsConstructor
class JobPositionService implements JobPositionAPI {

    private final JobPositionRepository jobPositionRepository;
    private final MachineAPI machineAPI;

    @Override
    public JobPositionDTO createJobPosition(CreateJobPositionDTO createJobPositionDTO) {
        JobPosition jobPosition = JobPositionMapper.mapToEntity(createJobPositionDTO);
        jobPosition = jobPositionRepository.save(jobPosition);

        // todo: createJobPosition is prepared to handle creation with assignment machine and/or requirement
        //  qualifications but this behavior of create method have to be consider and for sure implement after basic
        //  CRUD
        if (createJobPositionDTO.machineDTO() != null) {
            Machine machine =
                    machineAPI.getMachine(createJobPositionDTO.machineDTO().id());
            jobPosition.setMachine(machine);
        }

        if (createJobPositionDTO.qualificationDTOS() != null) {
            // todo /\
        }

        return JobPositionMapper.mapToDTO(jobPositionRepository.save(jobPosition));
    }

    @Override
    public Optional<JobPosition> findById(UUID id) {
        return jobPositionRepository.findById(id);
    }
}
