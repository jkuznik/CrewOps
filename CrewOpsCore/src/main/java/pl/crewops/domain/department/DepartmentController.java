package pl.crewops.domain.department;

import jakarta.validation.constraints.NotNull;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.crewops.dto.department.DepartmentDTO;
import pl.crewops.enums.ControllerURL;

@RestController
@RequiredArgsConstructor
@Validated
class DepartmentController {

    private final DepartmentAPI departmentAPI;

    @GetMapping(ControllerURL.DEPARTMENTS)
    public ResponseEntity<List<DepartmentDTO>> getDepartments() {
        return ResponseEntity.ok(departmentAPI.getDepartments());
    }

    @PostMapping(ControllerURL.DEPARTMENTS_DIDS)
    public ResponseEntity<Set<DepartmentDTO>> getDepartmentsByIds(@NotNull Set<UUID> ids) {
        return ResponseEntity.ok(departmentAPI.getDepartmentsIn(ids).stream()
                .map(DepartmentMapper::mapToDTO)
                .collect(Collectors.toSet()));
    }
}
