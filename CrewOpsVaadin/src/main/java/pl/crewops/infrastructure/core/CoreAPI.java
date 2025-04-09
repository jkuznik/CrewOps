package pl.crewops.infrastructure.core;

import java.util.List;
import java.util.Set;
import java.util.UUID;
import org.springframework.stereotype.Repository;
import pl.crewops.dto.employee.EmployeeDTO;
import pl.crewops.dto.qualification.QualificationDTO;

@Repository
public interface CoreAPI {

    List<EmployeeDTO> getEmployees();

    List<QualificationDTO> getQualifications(Set<UUID> qualificationIds);
}
