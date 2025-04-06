package pl.crewops.model.compositePK;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.Hibernate;

@Getter
@Setter
@Embeddable
public class EmployeeQualificationId implements Serializable {
    private static final long serialVersionUID = 1758502992767958280L;

    @NotNull
    @Column(name = "employee_id", nullable = false)
    private UUID employeeId;

    @NotNull
    @Column(name = "qualification_id", nullable = false)
    private UUID qualificationId;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        EmployeeQualificationId entity = (EmployeeQualificationId) o;
        return Objects.equals(this.qualificationId, entity.qualificationId)
                && Objects.equals(this.employeeId, entity.employeeId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(qualificationId, employeeId);
    }
}
