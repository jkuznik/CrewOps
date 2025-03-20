package pl.kuznik.entity.compositePK;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.Hibernate;

import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;

@Getter
@Setter
@Embeddable
public class EmployeeVehicleId implements Serializable {
    private static final long serialVersionUID = -4175339390866172073L;
    @NotNull
    @Column(name = "employee_id", nullable = false)
    private UUID employeeId;

    @NotNull
    @Column(name = "vehicle_id", nullable = false)
    private UUID vehicleId;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        EmployeeVehicleId entity = (EmployeeVehicleId) o;
        return Objects.equals(this.employeeId, entity.employeeId) &&
                Objects.equals(this.vehicleId, entity.vehicleId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(employeeId, vehicleId);
    }

}