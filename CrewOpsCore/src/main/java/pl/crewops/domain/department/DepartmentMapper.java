package pl.crewops.domain.department;

import pl.crewops.model.dto.department.DepartmentDTO;
import pl.crewops.model.tenantSchema.Department;

class DepartmentMapper {

    static DepartmentDTO mapToDTO(Department department) {
        return DepartmentDTO.builder()
                .id(department.getId())
                .name(department.getName())
                .build();
    }
}
