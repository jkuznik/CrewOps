package pl.crewops.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import lombok.*;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MachineType extends AbstractEntity {

    @Column(nullable = false, unique = true)
    private String name;
}
