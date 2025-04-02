package pl.kuznik.domain.qualification;

import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import pl.kuznik.entity.Qualification;

@Component
@RequiredArgsConstructor
public class QualificationAPI {

    private final QualificationService qualificationService;

    public Qualification getQualification(UUID qualificationId) {
        return qualificationService.getQualification(qualificationId);
    }
}
