package pl.crewops.model;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.LinkedHashSet;
import java.util.Set;
import lombok.*;
import org.hibernate.annotations.JdbcType;
import org.hibernate.dialect.PostgreSQLEnumJdbcType;
import pl.crewops.dto.vehicle.CreateVehicleDTO;
import pl.crewops.dto.vehicle.VehicleDTO;
import pl.crewops.enums.VehicleType;
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

    // TODO: change this to separate table in db
    @NotNull
    @Enumerated
    @JdbcType(PostgreSQLEnumJdbcType.class)
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

    public Vehicle mapToEntity(CreateVehicleDTO createVehicleDTO) {
        return Vehicle.builder()
                .make(createVehicleDTO.make())
                .model(createVehicleDTO.model())
                .vehicleType(createVehicleDTO.vehicleType())
                .year(createVehicleDTO.year())
                .vin(createVehicleDTO.vin())
                .registerNumber(createVehicleDTO.registerNumber())
                .broken(createVehicleDTO.broken())
                .build();
    }

    public VehicleDTO mapToDTO() {
        return VehicleDTO.builder()
                .id(this.getId())
                .make(this.getMake())
                .model(this.getModel())
                .vehicleType(this.getVehicleType())
                .year(this.getYear())
                .vin(this.getVin())
                .registerNumber(this.getRegisterNumber())
                .broken(this.getBroken())
                .build();
    }
}
