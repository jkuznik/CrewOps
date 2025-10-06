package pl.crewops.model.publicSchema;

import jakarta.persistence.*;
import java.util.UUID;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import pl.crewops.enums.CompanyStatus;
import pl.crewops.model.AbstractEntity;

@Entity
@Table(name = "tenant", schema = "public")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Tenant extends AbstractEntity {

    @Column(nullable = false, unique = true, updatable = false)
    private UUID companyId;

    @Column(nullable = false, unique = true, updatable = false)
    private String schemaName;

    @Column(nullable = false, unique = true)
    private String taxId;

    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    @Column(columnDefinition = "company_status", nullable = false)
    private CompanyStatus status;
}
