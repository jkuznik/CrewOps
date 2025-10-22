package pl.crewops.domain.jobPosition;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.crewops.enums.ControllerURL;
import pl.crewops.model.dto.jobPosition.JobPositionDTO;

@RestController
@RequiredArgsConstructor
public class JobPositionController {

    private final JobPositionAPI jobPositionAPI;

    @GetMapping(ControllerURL.JOB_POSITIONS)
    public ResponseEntity<List<JobPositionDTO>> getAllJobPositions() {
        return ResponseEntity.ok(jobPositionAPI.getAllJobPositions());
    }
}
