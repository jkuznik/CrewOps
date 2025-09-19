package pl.crewops.domain.qualification;

import pl.crewops.model.Employee;
import pl.crewops.model.Qualification;
import pl.crewops.model.dto.qualification.CreateQualificationDTO;
import pl.crewops.model.dto.qualification.QualificationDTO;

class QualificationMapper {

    public static Qualification mapToEntity(CreateQualificationDTO createQualificationDTO) {
        return Qualification.builder()
                .description(createQualificationDTO.description())
                .build();
    }

    public static QualificationDTO mapToDTO(Qualification qualification) {
        long count =
                qualification.getEmployees().stream().filter(Employee::isActive).count();

        return QualificationDTO.builder()
                .id(qualification.getId())
                .description(qualification.getDescription())
                .employeesAmount((int) count)
                .build();
    }
}
