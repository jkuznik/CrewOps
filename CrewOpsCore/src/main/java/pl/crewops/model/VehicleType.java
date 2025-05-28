package pl.crewops.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import lombok.*;
import pl.crewops.dto.vehicleType.VehicleTypeDTO;

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

    @Column(nullable = false, unique = true)
    private String type;

    public VehicleTypeDTO toDTO() {
        return VehicleTypeDTO.builder().id(this.getId()).name(this.getType()).build();
    }
}
