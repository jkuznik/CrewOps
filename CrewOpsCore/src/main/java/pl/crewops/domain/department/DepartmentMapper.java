package pl.crewops.domain.department;

import org.mapstruct.Mapper;
import pl.crewops.model.dto.department.DepartmentDTO;
import pl.crewops.model.tenantSchema.Department;

@Mapper(componentModel = "spring")
public interface DepartmentMapper {

    DepartmentDTO toDTO(Department entity);
}
