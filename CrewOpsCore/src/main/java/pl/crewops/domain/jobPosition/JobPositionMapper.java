package pl.crewops.domain.jobPosition;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import pl.crewops.domain.machine.MachineMapper;
import pl.crewops.domain.qualification.QualificationMapper;
import pl.crewops.model.dto.jobPosition.CreateJobPositionDTO;
import pl.crewops.model.dto.jobPosition.JobPositionDTO;
import pl.crewops.model.tenantSchema.JobPosition;

@Mapper(
        componentModel = "spring",
        uses = {MachineMapper.class, QualificationMapper.class})
public interface JobPositionMapper {

    @Mapping(target = "machine", ignore = true)
    @Mapping(target = "qualifications", ignore = true)
    JobPosition toEntity(CreateJobPositionDTO dto);

    JobPositionDTO toDTO(JobPosition entity);
}
