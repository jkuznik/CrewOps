package pl.crewops.model.dto.jobPosition;

import jakarta.validation.constraints.NotNull;
import java.util.Set;
import java.util.UUID;
import lombok.Builder;
import pl.crewops.model.dto.machine.MachineDTO;
import pl.crewops.model.dto.qualification.QualificationDTO;

@Builder
public record UpdateJobPositionDTO(
        @NotNull UUID id, String name, MachineDTO machineDTO, Set<QualificationDTO> qualifications) {}
