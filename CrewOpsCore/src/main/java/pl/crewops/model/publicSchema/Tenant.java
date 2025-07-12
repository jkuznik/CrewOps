package pl.crewops.model.publicSchema;

import jakarta.persistence.*;
import java.util.UUID;
import lombok.*;
import pl.crewops.model.AbstractEntity;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "tenant", schema = "public")
public class Tenant extends AbstractEntity {

    @Column(name = "company_id", nullable = false, unique = true)
    private UUID companyId;

    @Column(nullable = false, unique = true)
    private String schemaName;

    private boolean active;
}
