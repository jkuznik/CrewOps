package pl.crewops.model.tenantSchema;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import pl.crewops.enums.NoteType;
import pl.crewops.model.AbstractEntity;

@Getter
@Setter
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Note extends AbstractEntity {

    @Column(nullable = false)
    private LocalDate date;

    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    @Column(columnDefinition = "daily_note_type", nullable = false)
    private NoteType type;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "reported_by_employee_id", nullable = false, updatable = false)
    private Employee reportedByEmployeeId;

    @Size(max = 32767)
    @NotNull
    private String content;
}
