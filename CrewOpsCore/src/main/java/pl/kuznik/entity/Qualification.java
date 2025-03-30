package pl.kuznik.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.LinkedHashSet;
import java.util.Set;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "qualification")
public class Qualification extends AbstractEntity {

    @Size(max = 100)
    @NotNull
    private String name;

    private String description;

    @Builder.Default
    @JsonIgnore
    @ManyToMany(mappedBy = "qualifications")
    private Set<Employee> employees = new LinkedHashSet<>();
}
