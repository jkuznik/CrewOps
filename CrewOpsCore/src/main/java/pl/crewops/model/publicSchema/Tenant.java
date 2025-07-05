package pl.crewops.model.publicSchema;

import jakarta.persistence.*;
import pl.crewops.enums.TenantStatus;
import pl.crewops.model.AbstractEntity;

@Entity
@Table(schema = "public")
public class Tenant extends AbstractEntity {

    @Column(nullable = false, unique = true)
    private String name;

    @Column(nullable = false, unique = true)
    private String schemaName;

    private boolean active;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TenantStatus status;
}
