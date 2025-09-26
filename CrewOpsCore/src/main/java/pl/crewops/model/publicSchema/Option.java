package pl.crewops.model.publicSchema;

import jakarta.persistence.*;
import java.time.Instant;
import java.util.UUID;
import lombok.*;

@Entity
@Table(name = "option", schema = "public")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Option {

    @Column(nullable = false, unique = true)
    private String name;

    // the reason why this entity not extends AbstractEntity is caused by different id column configuration
    @Id
    @Column(name = "id", nullable = false)
    protected UUID id;

    @Version
    private Integer version;

    @Column(insertable = false, updatable = false)
    private Instant createdAt;

    @Column(insertable = false, updatable = false)
    private Instant updatedAt;
}
