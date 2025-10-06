package pl.crewops.domain.department;

import java.util.List;
import java.util.Set;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import pl.crewops.model.tenantSchema.Department;

interface DepartmentRepository extends JpaRepository<Department, UUID> {

    @Query("SELECT d FROM Department d ORDER BY d.name ASC")
    List<Department> findAllSortedByName();

    Set<Department> findAllByIdIn(Set<UUID> departmentIds);
}
