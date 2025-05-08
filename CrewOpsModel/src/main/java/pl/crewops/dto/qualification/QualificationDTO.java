package pl.crewops.dto.qualification;

import java.util.UUID;
import lombok.Builder;

@Builder
public record QualificationDTO(UUID id, String description, Integer employeesAmount) {}
