package pl.crewops.model;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.LinkedHashSet;
import java.util.Set;
import lombok.*;
import pl.crewops.utils.serializer.EmployeeSetSerializer;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "vehicle")
public class Vehicle extends AbstractEntity {
    @Size(max = 31)
    @NotNull
    @Column(updatable = false)
    private String make;

    @Size(max = 31)
    @NotNull
    @Column(updatable = false)
    private String model;

    @NotNull
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "vehicle_type_id", nullable = false)
    private VehicleType vehicleType;

    @NotNull
    @Column(updatable = false)
    private Integer year;

    @Size(max = 50)
    @Column(updatable = false)
    private String vin;

    @Size(max = 15)
    private String registerNumber;

    @NotNull
    private Boolean broken;

    @Builder.Default
    @JsonSerialize(using = EmployeeSetSerializer.class)
    @ManyToMany(mappedBy = "vehicles")
    private Set<Employee> employees = new LinkedHashSet<>();

    //    public VehicleDTO mapToDTO() {
    //        if (vehicleType != null) {
    //            return VehicleDTO.builder()
    //                    .id(this.getId())
    //                    .make(this.getMake())
    //                    .model(this.getModel())
    //                    .vehicleType(VehicleTypeDTO.builder()
    //                            .id(this.getVehicleType().getId())
    //                            .build())
    //                    .year(this.getYear())
    //                    .vin(this.getVin())
    //                    .registerNumber(this.getRegisterNumber())
    //                    .broken(this.getBroken())
    //                    .build();
    //
    //        } else {
    //            return VehicleDTO.builder()
    //                    .id(this.getId())
    //                    .make(this.getMake())
    //                    .model(this.getModel())
    //                    .vehicleType(VehicleTypeDTO.builder().name("ImplementThis").build())
    //                    .year(this.getYear())
    //                    .vin(this.getVin())
    //                    .registerNumber(this.getRegisterNumber())
    //                    .broken(this.getBroken())
    //                    .build();
    //        }
    //    }
}
