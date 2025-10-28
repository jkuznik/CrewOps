package pl.crewops.domain.jobPosition;

import java.util.Set;
import java.util.stream.Collectors;
import pl.crewops.model.dto.jobPosition.CreateJobPositionDTO;
import pl.crewops.model.dto.jobPosition.JobPositionDTO;
import pl.crewops.model.dto.machine.MachineDTO;
import pl.crewops.model.dto.machineType.MachineTypeDTO;
import pl.crewops.model.dto.qualification.QualificationDTO;
import pl.crewops.model.tenantSchema.JobPosition;

class JobPositionMapper {

    static JobPosition mapToEntity(CreateJobPositionDTO createJobPositionDTO) {
        return JobPosition.builder().name(createJobPositionDTO.name()).build();
    }

    static JobPositionDTO mapToDTO(JobPosition entity) {
        if (entity == null) {
            return null;
        }

        MachineDTO machineDTO = null;
        if (entity.getMachine() != null) {
            machineDTO = MachineDTO.builder()
                    .id(entity.getMachine().getId())
                    .make(entity.getMachine().getMake())
                    .model(entity.getMachine().getModel())
                    .year(entity.getMachine().getYear())
                    .machineType(MachineTypeDTO.builder()
                            .id(entity.getMachine().getMachineType().getId())
                            .name(entity.getMachine().getMachineType().getName())
                            .build())
                    .registerNumber(entity.getMachine().getRegisterNumber())
                    .broken(entity.getMachine().getBroken())
                    .vin(entity.getMachine().getVin())
                    .build();
        }

        return JobPositionDTO.builder()
                .id(entity.getId())
                .name(entity.getName())
                .machine(machineDTO)
                .qualifications(getQualifications(entity))
                .build();
    }

    private static Set<QualificationDTO> getQualifications(JobPosition jobPosition) {
        return jobPosition.getQualifications().stream()
                .map(role -> QualificationDTO.builder()
                        .id(role.getId())
                        .description(role.getDescription())
                        .build())
                .collect(Collectors.toSet());
    }
}
