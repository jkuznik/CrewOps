package pl.kuznik.domain.qualification;

import pl.kuznik.domain.qualification.dto.CreateQualificationDTO;
import pl.kuznik.domain.qualification.dto.QualificationDTO;
import pl.kuznik.entity.Qualification;

class QualificationMapper {

    public static Qualification mapToEntity(CreateQualificationDTO createQualificationDTO) {
        return Qualification.builder()
                .description(createQualificationDTO.description())
                .build();
    }

    public static QualificationDTO mapToDTO(Qualification qualification) {
        return QualificationDTO.builder()
                .id(qualification.getId())
                .description(qualification.getDescription())
                .employees(qualification.getEmployees())
                .build();
    }
}
