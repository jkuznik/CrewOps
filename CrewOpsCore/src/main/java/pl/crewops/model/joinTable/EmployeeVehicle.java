package pl.crewops.model.joinTable;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import pl.crewops.model.Employee;
import pl.crewops.model.Vehicle;
import pl.crewops.model.compositePK.EmployeeVehicleId;

@Getter
@Setter
@Entity
@Table(name = "employee_vehicle")
public class EmployeeVehicle {
    @EmbeddedId
    private EmployeeVehicleId id;

    @MapsId("employeeId")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "employee_id", nullable = false)
    private Employee employee;

    @MapsId("vehicleId")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "vehicle_id", nullable = false)
    private Vehicle vehicle;
}
