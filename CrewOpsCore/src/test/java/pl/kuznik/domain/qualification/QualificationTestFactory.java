package pl.kuznik.domain.qualification;

import pl.kuznik.domain.qualification.dto.CreateQualificationDTO;
import pl.kuznik.entity.Qualification;

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
