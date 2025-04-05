package pl.crewops.domain.qualification;

import pl.crewops.dto.qualification.CreateQualificationDTO;
import pl.crewops.model.Qualification;

class QualificationTestFactory {

    public static Qualification createQualification() {
        return Qualification.builder().description("foo1").build();
    }

    public static CreateQualificationDTO createCreateQualificationDTOWithDescription() {
        return CreateQualificationDTO.builder().description("foo1").build();
    }

    public static CreateQualificationDTO createCreateQualificationDTOWithoutDescription() {
        return CreateQualificationDTO.builder().build();
    }
}
