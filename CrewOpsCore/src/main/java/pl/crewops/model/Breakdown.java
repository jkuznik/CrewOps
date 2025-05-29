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
    @JoinColumn(name = "vehicle_id")
    private Vehicle vehicle;

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

    //    public BreakdownDTO toDTO() {
    //        return BreakdownDTO.builder()
    //                .id(this.getId())
    //                .description(this.getDescription())
    //                .vehicle(this.getVehicle().mapToDTO())
    //                .reportedBy(this.getReportedBy().mapToDTO())
    //                .repairedBy(this.getRepairedBy() != null ? this.getRepairedBy().mapToDTO() : null)
    //                .critical(this.isCritical())
    //                .solved(this.isSolved())
    //                .solvedAt(this.getSolvedAt())
    //                .build();
    //    }
}
