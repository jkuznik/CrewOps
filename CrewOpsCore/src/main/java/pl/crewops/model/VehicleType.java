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

    // TODO:
    // 1. Implement logic in case when there is no vehicle type for new vehicle (add new vehicle type)
    // 2. Implement logic when there is vehicle type in db for new vehicle (select component with available types or
    // etc.)
    // 3. Implement logic to delete vehicle type when last vehicle of that type is removed from db
    // 4. Implement CRUD for vehicle type domain
    // 5. Test coverage for vehicle type domain
    // 6. Consider to implement equals and hashcode method in ever entity and entityDTO

    @Column(nullable = false, unique = true)
    private String name;

    //    public VehicleTypeDTO toDTO() {
    //        return VehicleTypeDTO.builder().id(this.getId()).name(this.getName()).build();
    //    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof VehicleType that)) return false;
        return Objects.equals(getId(), that.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getName());
    }
}
