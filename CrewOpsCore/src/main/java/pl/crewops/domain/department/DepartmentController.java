package pl.crewops.domain.department;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
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
}
