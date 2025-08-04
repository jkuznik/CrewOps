package pl.crewops.dto.employee;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import lombok.Builder;
import pl.crewops.dto.auth.RoleDTO;
import pl.crewops.dto.machine.MachineDTO;
import pl.crewops.dto.qualification.QualificationDTO;

@Builder
public record EmployeeDTO(
        UUID id,
        String firstName,
        String lastName,
        LocalDate birthDate,
        String phoneNumber,
        String department,
        Set<RoleDTO> roles,
        boolean active,
        Set<QualificationDTO> qualifications,
        Set<MachineDTO> machines)
        implements Serializable {
    @Override
    public boolean equals(Object o) {
        if (!(o instanceof EmployeeDTO that)) return false;
        return Objects.equals(id(), that.id());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id());
    }
}
