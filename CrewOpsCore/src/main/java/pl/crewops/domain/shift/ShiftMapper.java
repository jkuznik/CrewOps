package pl.crewops.domain.shift;

import java.util.Set;
import org.mapstruct.Mapper;
import pl.crewops.domain.jobPosition.JobPositionMapperStruct;
import pl.crewops.model.dto.shift.CreateShiftDTO;
import pl.crewops.model.dto.shift.ShiftDTO;
import pl.crewops.model.tenantSchema.JobPosition;
import pl.crewops.model.tenantSchema.Shift;

@Mapper(
        componentModel = "spring",
        uses = {JobPositionMapperStruct.class})
public interface ShiftMapper {

    ShiftDTO toDTO(Shift shift);

    Shift toEntity(CreateShiftDTO createShiftDTO);

    default Shift toEntity(CreateShiftDTO createShiftDTO, Set<JobPosition> jobPositions) {
        Shift shift = toEntity(createShiftDTO);

        shift.setJobPositions(jobPositions);

        return shift;
    }
}
