package pl.crewops.model.tenantSchema;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;
import lombok.*;
import pl.crewops.model.AbstractEntity;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class ScheduleTemplateDay extends AbstractEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "schedule_template_id", nullable = false)
    private ScheduleTemplate scheduleTemplate;

    @Column(nullable = false)
    private int dayIndex;

    @Builder.Default
    @OneToMany(
            mappedBy = "scheduleTemplateDay",
            cascade = CascadeType.ALL,
            orphanRemoval = true,
            fetch = FetchType.EAGER)
    private List<ScheduleTemplateItem> items = new ArrayList<>();
}
