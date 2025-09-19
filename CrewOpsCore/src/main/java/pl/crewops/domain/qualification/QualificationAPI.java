package pl.crewops.domain.qualification;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import org.springframework.validation.annotation.Validated;
import pl.crewops.exception.domain.qualification.QualificationNotFoundException;
import pl.crewops.model.Qualification;
import pl.crewops.model.dto.qualification.CreateQualificationDTO;
import pl.crewops.model.dto.qualification.QualificationDTO;
import pl.crewops.model.dto.qualification.UpdateQualificationDTO;

@Validated
public interface QualificationAPI {

    QualificationDTO createQualification(@NotNull @Valid CreateQualificationDTO createQualificationDTO);

    Qualification getQualification(@NotNull UUID qualificationId) throws QualificationNotFoundException;

    QualificationDTO updateQualification(@NotNull @Valid UpdateQualificationDTO updateQualificationDTO);

    List<QualificationDTO> getAllQualifications(int page, int size);

    List<QualificationDTO> getQualificationsIn(@NotNull Set<UUID> qualificationIds);

    List<QualificationDTO> getAllQualificationsWithExpirationTimeByEmployeeId(@NotNull UUID employeeId);

    void deleteQualification(@NotNull UUID qualificationId);
}
