package pl.crewops.model.tenantSchema;

import jakarta.persistence.*;
import lombok.*;
import pl.crewops.model.AbstractEntity;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class ScheduleTemplateItem extends AbstractEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "shift_id", nullable = false)
    private Shift shift;

    @Column(nullable = false)
    private int startMinute;

    @Column(nullable = false)
    private int durationMinutes;
}
