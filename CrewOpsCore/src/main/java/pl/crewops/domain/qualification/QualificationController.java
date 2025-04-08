package pl.crewops.domain.qualification;

import static pl.crewops.enums.ControllerURL.*;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import pl.crewops.dto.qualification.CreateQualificationDTO;
import pl.crewops.dto.qualification.QualificationDTO;
import pl.crewops.dto.qualification.UpdateQualificationDTO;

@RestController
@RequiredArgsConstructor
@Validated
class QualificationController {

    private final QualificationService qualificationService;

    @PostMapping(QUALIFICATIONS)
    public ResponseEntity<QualificationDTO> createQualification(
            @RequestBody @Valid @NotNull CreateQualificationDTO createQualificationDTO) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(qualificationService.createQualification(createQualificationDTO));
    }

    @GetMapping(QUALIFICATIONS)
    public ResponseEntity<List<QualificationDTO>> getQualifications(
            @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "15") int size) {
        return ResponseEntity.status(HttpStatus.OK).body(qualificationService.getAllQualifications(page, size));
    }

    @PostMapping(QUALIFICATIONS_QIDS)
    public ResponseEntity<List<QualificationDTO>> getQualificationsByIds(
            @RequestBody @Valid @NotNull Set<UUID> qualificationIds) {
        return ResponseEntity.status(HttpStatus.OK).body(qualificationService.getQualificationsIn(qualificationIds));
    }

    @PatchMapping(QUALIFICATIONS_QID)
    public ResponseEntity<QualificationDTO> updateQualification(
            @PathVariable(QUALIFICATION_ID) UUID qualificationId, @RequestParam String description) {
        var updateQualificationDTO = new UpdateQualificationDTO(qualificationId, description);

        return ResponseEntity.status(HttpStatus.OK)
                .body(qualificationService.updateQualification(updateQualificationDTO));
    }

    @DeleteMapping(QUALIFICATIONS_QID)
    public ResponseEntity<Void> deleteQualification(@PathVariable(QUALIFICATION_ID) UUID qualificationId) {
        qualificationService.deleteQualification(qualificationId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
