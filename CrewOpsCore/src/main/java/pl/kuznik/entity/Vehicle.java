package pl.kuznik.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.Instant;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.UUID;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "vehicle")
public class Vehicle {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", nullable = false)
    private UUID id;

    @Size(max = 50)
    @Column(name = "vin", length = 50)
    private String vin;

    @Size(max = 31)
    @NotNull
    @Column(name = "make", nullable = false, length = 31)
    private String make;

    @Size(max = 31)
    @NotNull
    @Column(name = "model", nullable = false, length = 31)
    private String model;

    @NotNull
    @Column(name = "year", nullable = false)
    private Integer year;

    @Size(max = 15)
    @Column(name = "register_number", length = 15)
    private String registerNumber;

    @NotNull
    @Column(name = "broken", nullable = false)
    private Boolean broken = false;

    @Column(name = "created_at", nullable = false, insertable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false, insertable = false, updatable = false)
    private Instant updatedAt;

    @ManyToMany(mappedBy = "vehicles", fetch = FetchType.LAZY)
    private Set<Employee> employees = new LinkedHashSet<>();

    /*
     TODO [Reverse Engineering] create field to map the 'vehicle_type' column
     Available actions: Define target Java type | Uncomment as is | Remove column mapping
        @Column(name = "vehicle_type", columnDefinition = "vehicle_type_enum not null")
        private Object vehicleType;
    */
}
