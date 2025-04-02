package pl.kuznik.domain.qualification;

import static pl.kuznik.utils.enums.ControllerURL.*;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import pl.kuznik.domain.qualification.dto.CreateQualificationDTO;
import pl.kuznik.domain.qualification.dto.QualificationDTO;

@RestController
@RequiredArgsConstructor
@Validated
class QualificationController {

    private final QualificationService qualificationService;

    @PostMapping(QUALIFICATIONS)
    public ResponseEntity<QualificationDTO> create(
            @RequestBody @Valid @NotNull CreateQualificationDTO createQualificationDTO) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(qualificationService.createQualification(createQualificationDTO));
    }

    @GetMapping(QUALIFICATIONS)
    public ResponseEntity<List<QualificationDTO>> getAll() {
        return ResponseEntity.status(HttpStatus.OK).body(qualificationService.getAllQualifications());
    }
}
