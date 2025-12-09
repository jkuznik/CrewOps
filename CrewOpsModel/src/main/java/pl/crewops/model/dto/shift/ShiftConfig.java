package pl.crewops.model.dto.shift;

import lombok.Builder;
import pl.crewops.model.dto.employee.EmployeeDTO;
import pl.crewops.model.dto.jobPosition.JobPositionDTO;

@Builder
public record ShiftConfig(JobPositionDTO jopPosition, EmployeeDTO relatedEmployee, boolean critical) {}
