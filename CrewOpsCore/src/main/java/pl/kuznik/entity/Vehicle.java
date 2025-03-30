package pl.kuznik.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.Instant;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.UUID;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "vehicle")
public class Vehicle extends AbstractEntity {

    @Size(max = 50)
    private String vin;

    @Size(max = 31)
    @NotNull
    private String make;

    @Size(max = 31)
    @NotNull
    private String model;

    @NotNull
    private Integer year;

    @Size(max = 15)
    private String registerNumber;

    @NotNull
    private Boolean broken = false;

    @Builder.Default
    @JsonIgnore
    @ManyToMany(mappedBy = "vehicles")
    private Set<Employee> employees = new LinkedHashSet<>();

    /*
     TODO [Reverse Engineering] create field to map the 'vehicle_type' column
     Available actions: Define target Java type | Uncomment as is | Remove column mapping
        @Column(name = "vehicle_type", columnDefinition = "vehicle_type_enum not null")
        private Object vehicleType;
    */
}
