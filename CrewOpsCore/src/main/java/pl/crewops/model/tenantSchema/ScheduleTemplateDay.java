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

    @Column(nullable = false)
    private int dayIndex;

    @Builder.Default
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    @JoinColumn(name = "schedule_template_day_id")
    private List<ScheduleTemplateItem> items = new ArrayList<>();
}
