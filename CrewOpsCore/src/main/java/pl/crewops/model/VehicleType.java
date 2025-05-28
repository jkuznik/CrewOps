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

    @Column(nullable = false, unique = true)
    private String type;

    public VehicleTypeDTO toDTO() {
        return VehicleTypeDTO.builder().id(this.getId()).name(this.getType()).build();
    }
}
