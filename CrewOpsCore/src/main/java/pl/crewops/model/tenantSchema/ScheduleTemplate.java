package pl.crewops.model.tenantSchema;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import pl.crewops.enums.ScheduleTemplateType;
import pl.crewops.model.AbstractEntity;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class ScheduleTemplate extends AbstractEntity {

    @Column(nullable = false)
    private String name;

    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    @Column(columnDefinition = "schedule_template_type", nullable = false)
    private ScheduleTemplateType type;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "employee_id")
    private Employee author;

    @Column(nullable = false)
    private boolean isPrivate = true;

    @Builder.Default
    @OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "schedule_template_id")
    @OrderBy("dayIndex ASC")
    private List<ScheduleTemplateDay> days = new ArrayList<>();
}
