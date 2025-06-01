package pl.crewops.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import java.util.Objects;
import lombok.*;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VehicleType extends AbstractEntity {

    @Column(nullable = false, unique = true)
    private String name;

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof VehicleType that)) return false;
        return Objects.equals(getName(), that.getName());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getName());
    }
}
