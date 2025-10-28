package pl.crewops.model.dto.employee;

import java.io.Serializable;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import lombok.Builder;
import pl.crewops.model.dto.auth.RoleDTO;
import pl.crewops.model.dto.department.DepartmentDTO;
import pl.crewops.model.dto.machine.MachineDTO;
import pl.crewops.model.dto.qualification.QualificationDTO;

@Builder
public record EmployeeDTO(
        UUID id,
        String firstName,
        String lastName,
        String phoneNumber,
        String email,
        Set<RoleDTO> roles,
        Set<DepartmentDTO> departments,
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
