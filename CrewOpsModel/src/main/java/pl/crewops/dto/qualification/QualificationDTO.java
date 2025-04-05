package pl.crewops.dto.qualification;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import java.util.Set;
import java.util.UUID;
import lombok.Builder;
import pl.crewops.model.Employee;
import pl.crewops.utils.serializer.EmployeeSetSerializer;

@Builder
public record QualificationDTO(
        UUID id, String description, @JsonSerialize(using = EmployeeSetSerializer.class) Set<Employee> employees) {}
