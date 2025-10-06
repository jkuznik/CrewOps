package pl.crewops.model.tenantSchema;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.LinkedHashSet;
import java.util.Set;
import lombok.*;
import pl.crewops.model.AbstractEntity;
import pl.crewops.util.serializer.EmployeeSetSerializer;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Machine extends AbstractEntity {
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
    @JoinColumn(name = "machine_type_id", nullable = false)
    private MachineType machineType;

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
    @ManyToMany(mappedBy = "machines")
    private Set<Employee> employees = new LinkedHashSet<>();
}
