package pl.crewops.domain.qualification;

import pl.crewops.dto.qualification.CreateQualificationDTO;
import pl.crewops.dto.qualification.QualificationDTO;
import pl.crewops.model.Employee;
import pl.crewops.model.Qualification;

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
