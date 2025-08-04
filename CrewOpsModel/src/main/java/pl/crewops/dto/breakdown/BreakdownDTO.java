package pl.crewops.dto.breakdown;

import java.io.Serializable;
import java.time.Instant;
import java.util.Objects;
import java.util.UUID;
import lombok.Builder;
import pl.crewops.dto.employee.EmployeeDTO;
import pl.crewops.dto.machine.MachineDTO;

@Builder
public record BreakdownDTO(
        UUID id,
        String description,
        MachineDTO machine,
        EmployeeDTO reportedBy,
        EmployeeDTO repairedBy,
        boolean critical,
        boolean solved,
        Instant solvedAt)
        implements Serializable {
    @Override
    public boolean equals(Object o) {
        if (!(o instanceof BreakdownDTO that)) return false;
        return Objects.equals(id(), that.id());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id());
    }
}
