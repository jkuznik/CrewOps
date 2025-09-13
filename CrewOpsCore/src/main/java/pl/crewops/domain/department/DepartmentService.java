package pl.crewops.domain.department;

import java.util.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.crewops.dto.department.DepartmentDTO;
import pl.crewops.model.Department;

@Slf4j
@Service
@RequiredArgsConstructor
class DepartmentService implements DepartmentAPI {
    // TODO: create CRUD for this domain and dedicated view/grid to maintain this on fe side

    private final DepartmentRepository departmentRepository;

    @Override
    public Department getDepartment(UUID id) {
        // todo custom exception
        return departmentRepository.findById(id).orElseThrow(NoSuchElementException::new);
    }

    @Override
    @Transactional
    public List<DepartmentDTO> getDepartments() {
        return departmentRepository.findAllSortedByName().stream()
                .map(DepartmentMapper::mapToDTO)
                .toList();
    }

    @Override
    @Transactional
    public Set<Department> getDepartmentsIn(Set<UUID> departmentIds) {
        return departmentRepository.findAllByIdIn(departmentIds);
    }
}
