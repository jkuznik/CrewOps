package pl.crewops.domain.department;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.Set;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import pl.crewops.IntegrationTest;
import pl.crewops.model.tenantSchema.Department;

@Transactional
class DepartmentAPITest extends IntegrationTest {

    @Autowired
    protected DepartmentAPI departmentAPI;

    @Autowired
    private DepartmentRepository departmentRepository;

    @Test
    void getDepartments() {
        List<Department> result = departmentRepository.findAll();

        result.forEach(department -> {
            System.out.println(department.getName());
        });

        assertThat(result).hasSize(7);
    }

    @Test
    void getDepartmentsIn() {
        // given
        Set<UUID> departments = Set.of(UUID.fromString("d0000000-0000-0000-0000-000000000002"));

        // when
        Set<Department> result = departmentAPI.getDepartmentsIn(departments);

        // then
        assertThat(result).hasSize(1);
    }
}
