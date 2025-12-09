package pl.crewops.domain.qualification;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import pl.crewops.model.dto.qualification.CreateQualificationDTO;
import pl.crewops.model.dto.qualification.QualificationDTO;
import pl.crewops.model.tenantSchema.Employee;
import pl.crewops.model.tenantSchema.Qualification;

@Mapper(componentModel = "spring")
public interface QualificationMapper {

    Qualification toEntity(CreateQualificationDTO dto);

    @Mapping(target = "employeesAmount", source = "employees", qualifiedByName = "countActiveEmployees")
    QualificationDTO toDTO(Qualification qualification);

    @Named("countActiveEmployees")
    default int countActiveEmployees(java.util.Set<Employee> employees) {
        if (employees == null) return 0;
        return (int) employees.stream().filter(Employee::isActive).count();
    }
}
