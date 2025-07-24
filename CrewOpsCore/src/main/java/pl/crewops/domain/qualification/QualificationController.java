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
import org.springframework.web.server.ResponseStatusException;
import pl.crewops.dto.qualification.CreateQualificationDTO;
import pl.crewops.dto.qualification.QualificationDTO;
import pl.crewops.dto.qualification.UpdateQualificationDTO;
import pl.crewops.security.custom.permissionAnnotation.ManagerPermission;

@RestController
@RequiredArgsConstructor
@Validated
class QualificationController {

    private final QualificationAPI qualificationAPI;

    @PostMapping(QUALIFICATIONS)
    @ManagerPermission
    public ResponseEntity<QualificationDTO> createQualification(
            @RequestBody @Valid @NotNull CreateQualificationDTO createQualificationDTO) {
        return ResponseEntity.status(HttpStatus.OK).body(qualificationAPI.createQualification(createQualificationDTO));
    }

    @GetMapping(QUALIFICATIONS)
    public ResponseEntity<List<QualificationDTO>> getQualifications(
            @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "15") int size) {
        return ResponseEntity.status(HttpStatus.OK).body(qualificationAPI.getAllQualifications(page, size));
    }

    @PostMapping(QUALIFICATIONS_QIDS)
    public ResponseEntity<List<QualificationDTO>> getQualificationsByIds(
            @RequestBody @NotNull Set<UUID> qualificationIds) {
        return ResponseEntity.status(HttpStatus.OK).body(qualificationAPI.getQualificationsIn(qualificationIds));
    }

    @PatchMapping(QUALIFICATIONS_QID)
    @ManagerPermission
    public ResponseEntity<QualificationDTO> updateQualification(
            @PathVariable(QUALIFICATION_ID) UUID qualificationId,
            @RequestBody @Valid @NotNull UpdateQualificationDTO updateRequest) {

        if (!updateRequest.qualificationId().equals(qualificationId)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Path ID and body ID must match");
        }

        var updateQualificationDTO = UpdateQualificationDTO.builder()
                .qualificationId(qualificationId)
                .description(updateRequest.description())
                .build();

        return ResponseEntity.status(HttpStatus.OK).body(qualificationAPI.updateQualification(updateQualificationDTO));
    }

    @DeleteMapping(QUALIFICATIONS_QID)
    public ResponseEntity<Void> deleteQualification(@PathVariable(QUALIFICATION_ID) UUID qualificationId) {
        qualificationAPI.deleteQualification(qualificationId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
