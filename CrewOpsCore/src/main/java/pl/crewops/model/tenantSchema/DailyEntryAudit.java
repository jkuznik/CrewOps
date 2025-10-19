package pl.crewops.model.tenantSchema;

import com.fasterxml.jackson.databind.JsonNode;
import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import java.util.UUID;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import pl.crewops.enums.DailyEntryAuditType;
import pl.crewops.model.AbstractEntity;

@Getter
@Setter
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(uniqueConstraints = {@UniqueConstraint(columnNames = {"daily_entry_id", "created_at"})})
public class DailyEntryAudit extends AbstractEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "daily_entry_id", nullable = false)
    private DailyEntry dailyEntry;

    @Column(name = "action_by_employee_id", nullable = false, updatable = false)
    private UUID actionByEmployeeId;

    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    @Column(columnDefinition = "daily_entry_event_type", nullable = false)
    private DailyEntryAuditType eventType;

    @JdbcTypeCode(SqlTypes.JSON)
    private JsonNode payload;

    @Size(max = 127)
    private String comment;
}
