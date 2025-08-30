package pl.crewops.domain.department;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

import java.util.List;
import java.util.Set;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import pl.crewops.dto.department.DepartmentDTO;
import pl.crewops.model.Department;

class DepartmentServiceTest {

    private DepartmentService departmentService;

    @Mock
    private DepartmentRepository departmentRepository;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        departmentService = new DepartmentService(departmentRepository);
    }

    @Test
    void getDepartments_shouldReturnMappedDTOs() {
        // given
        Department dept1 = Department.builder().name("Accounting").build();
        dept1.setId(UUID.randomUUID());

        Department dept2 = Department.builder().name("HR").build();
        dept2.setId(UUID.randomUUID());

        when(departmentRepository.findAllSortedByName()).thenReturn(List.of(dept1, dept2));

        // when
        List<DepartmentDTO> result = departmentService.getDepartments();

        // then
        assertThat(result).hasSize(2);
        assertThat(result.get(0).id()).isEqualTo(dept1.getId());
        assertThat(result.get(0).name()).isEqualTo(dept1.getName());
        assertThat(result.get(1).id()).isEqualTo(dept2.getId());
        assertThat(result.get(1).name()).isEqualTo(dept2.getName());

        verify(departmentRepository).findAllSortedByName();
    }

    @Test
    void getDepartmentsIn_shouldReturnDepartmentsByIds() {
        // given
        UUID id1 = UUID.randomUUID();
        UUID id2 = UUID.randomUUID();
        Department dept1 = Department.builder().name("Accounting").build();
        dept1.setId(id1);
        Department dept2 = Department.builder().name("HR").build();
        dept2.setId(id2);

        when(departmentRepository.findAllByIdIn(Set.of(id1, id2))).thenReturn(Set.of(dept1, dept2));

        // when
        Set<Department> result = departmentService.getDepartmentsIn(Set.of(id1, id2));

        // then
        assertThat(result).containsExactlyInAnyOrder(dept1, dept2);
        verify(departmentRepository).findAllByIdIn(Set.of(id1, id2));
    }
}
