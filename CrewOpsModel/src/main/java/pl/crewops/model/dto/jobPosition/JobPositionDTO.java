package pl.crewops.model.dto.jobPosition;

import java.io.Serializable;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import lombok.Builder;
import pl.crewops.model.dto.machine.MachineDTO;
import pl.crewops.model.dto.qualification.QualificationDTO;

@Builder
public record JobPositionDTO(UUID id, String name, MachineDTO machine, Set<QualificationDTO> qualifications)
        implements Serializable {

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof JobPositionDTO that)) return false;
        return Objects.equals(id(), that.id());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id());
    }
}
