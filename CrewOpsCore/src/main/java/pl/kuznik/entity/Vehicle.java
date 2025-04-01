package pl.kuznik.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.LinkedHashSet;
import java.util.Set;
import lombok.*;
import org.hibernate.annotations.JdbcType;
import org.hibernate.dialect.PostgreSQLEnumJdbcType;
import pl.kuznik.utils.enums.VehicleType;
import pl.kuznik.utils.serializer.EmployeeSetSerializer;

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

    @Enumerated
    @JdbcType(PostgreSQLEnumJdbcType.class)
    @Column(nullable = false, columnDefinition = "vehicle_type_enum not null")
    private VehicleType vehicleType;

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
    private Boolean broken;

    @Builder.Default
    @JsonSerialize(using = EmployeeSetSerializer.class)
    @ManyToMany(mappedBy = "vehicles")
    private Set<Employee> employees = new LinkedHashSet<>();
}
