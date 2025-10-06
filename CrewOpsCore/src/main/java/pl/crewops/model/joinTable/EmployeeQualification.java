package pl.crewops.model.joinTable;

import jakarta.persistence.*;
import java.time.Instant;
import java.util.Objects;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import pl.crewops.model.compositePK.EmployeeQualificationId;
import pl.crewops.model.tenantSchema.Employee;
import pl.crewops.model.tenantSchema.Qualification;

@Getter
@Setter
@Entity
@Table(name = "employee_qualification")
public class EmployeeQualification {
    @EmbeddedId
    private EmployeeQualificationId id;

    @MapsId("employeeId")
    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "employee_id", nullable = false)
    private Employee employee;

    @MapsId("qualificationId")
    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "qualification_id", nullable = false)
    private Qualification qualification;

    @Column(name = "expired_at")
    private Instant expiredAt;

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof EmployeeQualification that)) return false;
        return Objects.equals(getId(), that.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getId());
    }
}
