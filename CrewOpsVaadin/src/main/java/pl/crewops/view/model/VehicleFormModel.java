package pl.crewops.view.model;

import java.util.UUID;
import lombok.*;
import pl.crewops.enums.VehicleType;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VehicleFormModel {
    private UUID id;
    private String make;
    private String model;
    private VehicleType vehicleType;
    private Integer year;
    private String vin;
    private String registrationNumber;
    private Boolean broken;
}
