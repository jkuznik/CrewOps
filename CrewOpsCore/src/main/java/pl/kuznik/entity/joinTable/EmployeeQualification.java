package pl.kuznik.entity.joinTable;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import pl.kuznik.entity.Employee;
import pl.kuznik.entity.Qualification;
import pl.kuznik.entity.compositePK.EmployeeQualificationId;

import java.time.Instant;

@Getter
@Setter
@Entity
@Table(name = "employee_qualification")
public class EmployeeQualification {
    @EmbeddedId
    private EmployeeQualificationId id;

    @MapsId("employeeId")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "employee_id", nullable = false)
    private Employee employee;

    @MapsId("qualificationId")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "qualification_id", nullable = false)
    private Qualification qualification;

    @Column(name = "expired_at")
    private Instant expiredAt;

}