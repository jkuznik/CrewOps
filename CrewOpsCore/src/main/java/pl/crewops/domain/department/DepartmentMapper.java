package pl.crewops.domain.department;

import pl.crewops.model.Department;
import pl.crewops.model.dto.department.DepartmentDTO;

class DepartmentMapper {

    static DepartmentDTO mapToDTO(Department department) {
        return DepartmentDTO.builder()
                .id(department.getId())
                .name(department.getName())
                .build();
    }
}
