package pl.crewops.model.tenantSchema;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import java.util.ArrayList;
import java.util.List;
import lombok.*;
import pl.crewops.model.AbstractEntity;
import pl.crewops.util.serializer.JobPositionSetSerializer;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Shift extends AbstractEntity {

    @Size(max = 63)
    @Column(nullable = false)
    private String name;

    @Builder.Default
    @JsonSerialize(using = JobPositionSetSerializer.class)
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "shift_job_position",
            joinColumns = @JoinColumn(name = "shift_id"),
            inverseJoinColumns = @JoinColumn(name = "job_position_id"))
    private List<JobPosition> jobPositions = new ArrayList<>();

    @Column(nullable = false)
    private String color;
}
