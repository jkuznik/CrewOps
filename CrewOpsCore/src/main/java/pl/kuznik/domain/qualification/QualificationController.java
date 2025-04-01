package pl.kuznik.domain.qualification;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.kuznik.domain.qualification.dto.CreateQualificationDTO;
import pl.kuznik.entity.Qualification;
import pl.kuznik.utils.enums.ControllerURL;

@RestController
@RequestMapping(ControllerURL.QUALIFICATIONS)
@RequiredArgsConstructor
@Validated
public class QualificationController {

    private final QualificationService qualificationService;

    @PostMapping("create")
    public ResponseEntity<Qualification> create(
            @RequestBody @Valid @NotNull CreateQualificationDTO createQualificationDTO) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(qualificationService.createQualification(createQualificationDTO));
    }
}
