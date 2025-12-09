package pl.crewops.model.joinTable;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import pl.crewops.model.compositePK.SJPID;
import pl.crewops.model.tenantSchema.Employee;
import pl.crewops.model.tenantSchema.JobPosition;
import pl.crewops.model.tenantSchema.Shift;

@Entity
@Table(name = "shift_job_position")
@Getter
@Setter
public class ShiftJobPosition {

    @EmbeddedId
    private SJPID id;

    @MapsId("shiftId")
    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "shift_id", nullable = false)
    private Shift shift;

    @MapsId("jobPositionId")
    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "job_position_id", nullable = false)
    private JobPosition jobPosition;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "assigned_employee_id")
    private Employee assignedEmployee;

    private boolean critical;

    public ShiftJobPosition() {}

    public ShiftJobPosition(SJPID id) {
        this.id = id;
    }
}
