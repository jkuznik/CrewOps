package pl.crewops.domain.qualification;

import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import pl.crewops.exception.QualificationNotFoundException;
import pl.crewops.model.Qualification;

@Component
@RequiredArgsConstructor
public class QualificationAPI {

    private final QualificationService qualificationService;

    public Qualification getQualification(UUID qualificationId) throws QualificationNotFoundException {
        return qualificationService.getQualification(qualificationId);
    }
}
