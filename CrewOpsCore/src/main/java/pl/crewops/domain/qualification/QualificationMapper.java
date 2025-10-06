package pl.crewops.domain.qualification;

import pl.crewops.model.dto.qualification.CreateQualificationDTO;
import pl.crewops.model.dto.qualification.QualificationDTO;
import pl.crewops.model.tenantSchema.Employee;
import pl.crewops.model.tenantSchema.Qualification;

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
