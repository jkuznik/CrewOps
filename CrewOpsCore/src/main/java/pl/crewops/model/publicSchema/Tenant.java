package pl.crewops.model.publicSchema;

import jakarta.persistence.*;
import java.util.UUID;
import lombok.*;
import pl.crewops.model.AbstractEntity;

@Entity
@Table(name = "tenant", schema = "public")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Tenant extends AbstractEntity {

    @Column(nullable = false, unique = true)
    private UUID companyId;

    @Column(nullable = false, unique = true)
    private String schemaName;

    @Column(nullable = false, unique = true)
    private String taxId;

    private boolean active;
}
