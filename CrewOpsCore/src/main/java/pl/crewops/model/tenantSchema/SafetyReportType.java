package pl.crewops.model.tenantSchema;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;
import pl.crewops.model.AbstractEntity;

@Getter
@Setter
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "srt")
public class SafetyReportType extends AbstractEntity {

    @NotNull
    @Column(unique = true, nullable = false)
    @Size(max = 100)
    private String name;

    private boolean isIncident;

    private boolean isHazard;

    private boolean requiresAction;
}
