package pl.crewops.model.dto.jobPosition;

import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import lombok.Builder;
import pl.crewops.model.dto.machine.MachineDTO;
import pl.crewops.model.dto.qualification.QualificationDTO;

@Builder
public record JobPositionDTO(UUID id, String name, MachineDTO machine, Set<QualificationDTO> qualifications) {

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof JobPositionDTO that)) return false;
        return Objects.equals(name(), that.name());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(name());
    }
}
