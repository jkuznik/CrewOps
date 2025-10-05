package pl.crewops.model.tenantSchema;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import java.util.LinkedHashSet;
import java.util.Set;
import lombok.*;
import pl.crewops.model.AbstractEntity;
import pl.crewops.model.dto.qualification.CreateQualificationDTO;
import pl.crewops.model.dto.qualification.QualificationDTO;
import pl.crewops.util.serializer.EmployeeSetSerializer;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Qualification extends AbstractEntity {

    @NotNull
    @Column(unique = true)
    private String description;

    @Builder.Default
    @JsonSerialize(using = EmployeeSetSerializer.class)
    @ManyToMany(mappedBy = "qualifications")
    private Set<Employee> employees = new LinkedHashSet<>();

    public Qualification mapToEntity(CreateQualificationDTO createQualificationDTO) {
        return Qualification.builder()
                .description(createQualificationDTO.description())
                .build();
    }

    public QualificationDTO mapToDTO() {
        return QualificationDTO.builder()
                .id(this.getId())
                .description(this.getDescription())
                .employeesAmount(this.getEmployees().size())
                .build();
    }
}
