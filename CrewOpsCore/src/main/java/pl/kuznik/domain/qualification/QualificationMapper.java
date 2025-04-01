package pl.kuznik.domain.qualification;

import pl.kuznik.domain.qualification.dto.CreateQualificationDTO;
import pl.kuznik.entity.Qualification;

public class QualificationMapper {

    static public Qualification mapToEntity(CreateQualificationDTO createQualificationDTO) {
        return Qualification.builder()
                .name(createQualificationDTO.name())
                .description(createQualificationDTO.description())
                .build();
    }
}
