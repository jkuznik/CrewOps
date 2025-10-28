package pl.crewops.domain.jobPosition;

import static pl.crewops.enums.ControllerURL.*;

import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.crewops.model.dto.jobPosition.CreateJobPositionDTO;
import pl.crewops.model.dto.jobPosition.JobPositionDTO;
import pl.crewops.model.dto.jobPosition.UpdateJobPositionDTO;
import pl.crewops.security.custom.permissionAnnotation.ManagerPermission;

@RestController
@RequiredArgsConstructor
class JobPositionController {

    private final JobPositionAPI jobPositionAPI;

    @PostMapping(JOB_POSITIONS)
    @ManagerPermission
    public ResponseEntity<JobPositionDTO> createJobPosition(@RequestBody CreateJobPositionDTO createJobPositionDTO) {
        return ResponseEntity.ok(jobPositionAPI.createJobPosition(createJobPositionDTO));
    }

    @GetMapping(JOB_POSITIONS)
    public ResponseEntity<List<JobPositionDTO>> getAllJobPositions() {
        return ResponseEntity.ok(jobPositionAPI.getAllJobPositions());
    }

    @PatchMapping(JOB_POSITIONS)
    @ManagerPermission
    public ResponseEntity<JobPositionDTO> updateJobPositions(@RequestBody UpdateJobPositionDTO updateJobPositionDTO) {
        return ResponseEntity.ok(jobPositionAPI.updateJobPosition(updateJobPositionDTO));
    }

    @DeleteMapping(JOB_POSITIONS_JID)
    @ManagerPermission
    public ResponseEntity<Void> deleteJobPosition(@PathVariable(JOB_POSITIONS_ID) UUID id) {
        jobPositionAPI.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
