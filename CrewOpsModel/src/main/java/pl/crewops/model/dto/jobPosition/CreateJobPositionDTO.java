package pl.crewops.model.dto.jobPosition;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.Set;
import lombok.Builder;
import pl.crewops.model.dto.machine.MachineDTO;
import pl.crewops.model.dto.qualification.QualificationDTO;

@Builder
public record CreateJobPositionDTO(
        @NotNull @Size(max = 255) String name, MachineDTO machineDTO, Set<QualificationDTO> qualificationDTOS) {}
