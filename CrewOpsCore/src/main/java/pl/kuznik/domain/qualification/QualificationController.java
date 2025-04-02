package pl.kuznik.domain.qualification;

import static pl.kuznik.utils.enums.ControllerURL.*;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import pl.kuznik.domain.qualification.dto.CreateQualificationDTO;
import pl.kuznik.domain.qualification.dto.QualificationDTO;
import pl.kuznik.domain.qualification.dto.UpdateQualificationDTO;

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
    public ResponseEntity<List<QualificationDTO>> getQualifications() {
        return ResponseEntity.status(HttpStatus.OK).body(qualificationService.getAllQualifications());
    }

    @PatchMapping(QUALIFICATIONS_QID)
    public ResponseEntity<QualificationDTO> updateQualification(
            @PathVariable(QUALIFICATION_ID) UUID qualificationId, @RequestParam String description) {
        var updateQualificationDTO = new UpdateQualificationDTO(qualificationId, description);

        return ResponseEntity.status(HttpStatus.OK)
                .body(qualificationService.updateQualification(updateQualificationDTO));
    }
}
