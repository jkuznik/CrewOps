package pl.crewops.model.compositePK;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.NotNull;
import java.io.Serial;
import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.proxy.HibernateProxy;

@Getter
@Setter
@NoArgsConstructor
@Embeddable
public class SJPID implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @NotNull
    @Column(name = "shift_id", nullable = false)
    private UUID shiftId;

    @NotNull
    @Column(name = "job_position_id", nullable = false)
    private UUID jobPositionId;

    public SJPID(UUID jobPositionId, UUID shiftId) {
        this.jobPositionId = jobPositionId;
        this.shiftId = shiftId;
    }

    @Override
    public final boolean equals(Object o) {
        if (this == o) return true;
        if (o == null) return false;
        Class<?> oEffectiveClass = o instanceof HibernateProxy
                ? ((HibernateProxy) o).getHibernateLazyInitializer().getPersistentClass()
                : o.getClass();
        Class<?> thisEffectiveClass = this instanceof HibernateProxy
                ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass()
                : this.getClass();
        if (thisEffectiveClass != oEffectiveClass) return false;
        SJPID sjpid = (SJPID) o;
        return getShiftId() != null
                && Objects.equals(getShiftId(), sjpid.getShiftId())
                && getJobPositionId() != null
                && Objects.equals(getJobPositionId(), sjpid.getJobPositionId());
    }

    @Override
    public final int hashCode() {
        return Objects.hash(shiftId, jobPositionId);
    }
}
