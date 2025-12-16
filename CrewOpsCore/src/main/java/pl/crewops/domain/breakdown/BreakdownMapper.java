package pl.crewops.domain.breakdown;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import pl.crewops.domain.employee.EmployeeMapper;
import pl.crewops.domain.machine.MachineMapper;
import pl.crewops.model.dto.breakdown.BreakdownDTO;
import pl.crewops.model.tenantSchema.Breakdown;

@Mapper(
        componentModel = "spring",
        uses = {MachineMapper.class, EmployeeMapper.class})
public interface BreakdownMapper {

    @Mapping(target = "reportedAt", source = "createdAt")
    BreakdownDTO toDTO(Breakdown breakdown);
}
