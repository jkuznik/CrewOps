package pl.crewops.domain.department;

import jakarta.validation.constraints.NotNull;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import org.springframework.validation.annotation.Validated;
import pl.crewops.model.dto.department.DepartmentDTO;
import pl.crewops.model.tenantSchema.Department;

@Validated
public interface DepartmentAPI {

    Department getDepartment(@NotNull UUID id);

    List<DepartmentDTO> getDepartments();

    Set<Department> getDepartmentsIn(@NotNull Set<UUID> departmentIds);

    DepartmentMapper getMapper();
}
