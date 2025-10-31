package pl.crewops.model.tenantSchema;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import pl.crewops.enums.DailyNoteType;
import pl.crewops.model.AbstractEntity;

@Getter
@Setter
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DailyNote extends AbstractEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "daily_entry_id")
    private DailyEntry dailyEntry;

    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    @Column(columnDefinition = "daily_note_type", nullable = false)
    private DailyNoteType type;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "reported_by_employee_id", nullable = false, updatable = false)
    private Employee reportedByEmployeeId;

    @Size(max = 32767)
    @NotNull
    private String content;
}
