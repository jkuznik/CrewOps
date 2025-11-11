package pl.crewops.domain.jobPosition;

import org.mapstruct.Mapper;
import pl.crewops.model.dto.jobPosition.JobPositionDTO;
import pl.crewops.model.tenantSchema.JobPosition;

@Mapper(componentModel = "spring")
public interface JobPositionMapperStruct {

    JobPositionDTO toDTO(JobPosition jobPosition);
}
