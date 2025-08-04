package pl.crewops.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import java.time.Instant;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "breakdown")
public class Breakdown extends AbstractEntity {

    @NotNull
    private String description;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "machine_id")
    private Machine machine;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "reported_by_id", nullable = false)
    private Employee reportedBy;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "repaired_by_id")
    private Employee repairedBy;

    @Column(updatable = false, nullable = false)
    private boolean critical;

    @Column(nullable = false)
    private boolean solved;

    private Instant solvedAt;
}
