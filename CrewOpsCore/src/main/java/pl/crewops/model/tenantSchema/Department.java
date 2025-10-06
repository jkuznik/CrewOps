package pl.crewops.model.tenantSchema;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.ManyToMany;
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
public class Department extends AbstractEntity {

    @NotNull
    @Size(max = 63)
    @Column(unique = true)
    private String name;

    @Builder.Default
    @JsonSerialize(using = EmployeeSetSerializer.class)
    @ManyToMany(mappedBy = "departments")
    private Set<Employee> employees = new LinkedHashSet<>();
}
