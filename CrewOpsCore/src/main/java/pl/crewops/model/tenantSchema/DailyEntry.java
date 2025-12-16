package pl.crewops.model.tenantSchema;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import pl.crewops.enums.DailyAttendanceStatus;
import pl.crewops.enums.DailyEntryStatus;
import pl.crewops.model.AbstractEntity;

@Getter
@Setter
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(
        uniqueConstraints = {
            // Unique constraint ensuring one entry per employee per day
            @UniqueConstraint(columnNames = {"employee_id", "entry_date"})
        })
public class DailyEntry extends AbstractEntity {

    @Column(nullable = false, updatable = false)
    private UUID employeeId;

    @Column(nullable = false)
    private LocalDate entryDate;

    @Column(nullable = false)
    private Instant startTime;

    private Instant endTime;

    @Builder.Default
    @Column(precision = 7, scale = 4, nullable = false)
    private BigDecimal overtime = BigDecimal.ZERO;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "job_position_id")
    private JobPosition jobPosition;

    @Builder.Default
    @OneToMany(fetch = FetchType.EAGER, mappedBy = "dailyEntry", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<DailyEntryAudit> auditEvents = new HashSet<>();

    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    @Column(columnDefinition = "daily_attendance")
    private DailyAttendanceStatus attendance;

    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    @Column(columnDefinition = "daily_status", nullable = false)
    private DailyEntryStatus status;
}
