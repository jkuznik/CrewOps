package pl.crewops.domain.qualification;

import java.util.UUID;
import pl.crewops.dto.qualification.CreateQualificationDTO;
import pl.crewops.dto.qualification.UpdateQualificationDTO;
import pl.crewops.model.Qualification;

class QualificationTestFactory {

    public static Qualification qualification() {
        return Qualification.builder().description("description").build();
    }

    public static CreateQualificationDTO createQualificationDTOWithDescription() {
        return CreateQualificationDTO.builder().description("description").build();
    }

    public static CreateQualificationDTO createQualificationDTOWithoutDescription() {
        return CreateQualificationDTO.builder().build();
    }

    public static UpdateQualificationDTO updateQualificationDTOWithDescription() {
        return UpdateQualificationDTO.builder()
                .qualificationId(UUID.fromString("11111111-1111-1111-1111-111111111111"))
                .description("description")
                .build();
    }

    public static UpdateQualificationDTO updateQualificationDTOWithoutDescription() {
        return UpdateQualificationDTO.builder().build();
    }
}
