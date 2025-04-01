package pl.kuznik.domain.qualification.dto;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import java.util.Set;
import java.util.UUID;
import lombok.Builder;
import pl.kuznik.entity.Employee;
import pl.kuznik.utils.serializer.EmployeeSetSerializer;

@Builder
public record QualificationDTO(
        UUID id, String description, @JsonSerialize(using = EmployeeSetSerializer.class) Set<Employee> employees) {}
