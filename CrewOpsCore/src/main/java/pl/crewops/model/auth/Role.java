package pl.crewops.model.auth;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import lombok.*;
import pl.crewops.model.AbstractEntity;

@Entity
@Getter
@Setter
public class Role extends AbstractEntity {

    @Column(nullable = false, unique = true)
    private String name;
}
