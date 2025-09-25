package pl.crewops.model.publicSchema;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.*;
import pl.crewops.model.AbstractEntity;

@Entity
@Table(name = "option", schema = "public")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Option extends AbstractEntity {

    @Column(nullable = false, unique = true)
    private String name;
}
