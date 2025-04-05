package pl.crewops.domain.qualification;

import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import pl.crewops.model.Qualification;

@Component
@RequiredArgsConstructor
public class QualificationAPI {

    private final QualificationService qualificationService;

    public Qualification getQualification(UUID qualificationId) {
        return qualificationService.getQualification(qualificationId);
    }
}
