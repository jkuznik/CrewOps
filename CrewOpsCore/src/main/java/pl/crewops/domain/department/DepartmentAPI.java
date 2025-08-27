package pl.crewops.domain.department;

import jakarta.validation.constraints.NotNull;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import org.springframework.validation.annotation.Validated;
import pl.crewops.dto.department.DepartmentDTO;
import pl.crewops.model.Department;

@Validated
public interface DepartmentAPI {

    List<DepartmentDTO> getDepartments();

    Set<Department> getDepartmentsIn(@NotNull Set<UUID> departmentIds);
}
