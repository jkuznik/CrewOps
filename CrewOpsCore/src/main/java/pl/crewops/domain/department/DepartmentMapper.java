package pl.crewops.domain.department;

import pl.crewops.dto.department.DepartmentDTO;
import pl.crewops.model.Department;

class DepartmentMapper {

    static DepartmentDTO mapToDTO(Department department) {
        return DepartmentDTO.builder()
                .id(department.getId())
                .name(department.getName())
                .build();
    }
}
