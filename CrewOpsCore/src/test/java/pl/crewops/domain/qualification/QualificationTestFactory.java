package pl.crewops.domain.qualification;

import java.util.UUID;
import pl.crewops.dto.qualification.CreateQualificationDTO;
import pl.crewops.dto.qualification.UpdateQualificationDTO;
import pl.crewops.model.Qualification;

class QualificationTestFactory {

    public static Qualification createQualification() {
        return Qualification.builder().description("description").build();
    }

    public static CreateQualificationDTO createCreateQualificationDTOWithDescription() {
        return CreateQualificationDTO.builder().description("description").build();
    }

    public static CreateQualificationDTO createCreateQualificationDTOWithoutDescription() {
        return CreateQualificationDTO.builder().build();
    }

    public static UpdateQualificationDTO createUpdateQualificationDTOWithDescription() {
        return UpdateQualificationDTO.builder()
                .qualificationId(UUID.fromString("11111111-1111-1111-1111-111111111111"))
                .description("description")
                .build();
    }

    public static UpdateQualificationDTO createUpdateQualificationDTOWithoutDescription() {
        return UpdateQualificationDTO.builder().build();
    }
}
