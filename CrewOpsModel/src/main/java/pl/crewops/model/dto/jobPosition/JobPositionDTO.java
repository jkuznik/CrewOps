package pl.crewops.model.dto.jobPosition;

import java.util.Set;
import java.util.UUID;
import lombok.Builder;
import pl.crewops.model.dto.machine.MachineDTO;
import pl.crewops.model.dto.qualification.QualificationDTO;

@Builder
public record JobPositionDTO(UUID id, String name, MachineDTO machine, Set<QualificationDTO> qualifications) {}
