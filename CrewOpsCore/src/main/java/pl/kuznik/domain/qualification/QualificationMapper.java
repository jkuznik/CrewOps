package pl.kuznik.domain.qualification;

import pl.kuznik.domain.qualification.dto.CreateQualificationDTO;
import pl.kuznik.entity.Qualification;

class QualificationMapper {

    public static Qualification mapToEntity(CreateQualificationDTO createQualificationDTO) {
        return Qualification.builder()
                .description(createQualificationDTO.description())
                .build();
    }
}
