// package pl.crewops.model.tenantSchema;
//
// import jakarta.persistence.*;
// import jakarta.validation.constraints.NotNull;
// import jakarta.validation.constraints.Size;
// import java.util.LinkedHashSet;
// import java.util.Set;
// import lombok.*;
// import org.hibernate.annotations.JdbcTypeCode;
// import org.hibernate.type.SqlTypes;
// import pl.crewops.enums.SafetyReportStatus;
// import pl.crewops.model.AbstractEntity;
//
// @Getter
// @Setter
// @Entity
// @Builder
// @NoArgsConstructor
// @AllArgsConstructor
// public class SafetyReport extends AbstractEntity {
//
//    // todo: update this entity after 'sector' feature implements
//
//    @Size(max = 32767)
//    @NotNull
//    private String content;
//
//    @Builder.Default
//    @ManyToMany(fetch = FetchType.EAGER)
//    @JoinTable(
//            name = "safety_report_job_position",
//            joinColumns = @JoinColumn(name = "safety_report_id"),
//            inverseJoinColumns = @JoinColumn(name = "job_position_id"))
//    private Set<JobPosition> jobPositions = new LinkedHashSet<>();
//
//    @Builder.Default
//    @ManyToMany(fetch = FetchType.EAGER)
//    @JoinTable(
//            name = "safety_report_department",
//            joinColumns = @JoinColumn(name = "safety_report_id"),
//            inverseJoinColumns = @JoinColumn(name = "department_id"))
//    private Set<Department> departments = new LinkedHashSet<>();
//
//    // to posłuży do logiki czy dany wpis ma charakter informujący (wydarzył się wypadek), ostrzegający (zauważone
//    // zagrożenie)
//    // lub zgłoszenie o nie przestrzeganiu bhp (np. przez pracodawce - brak środków ochrony indywidualnej)
//    // na potrzeby informacji dodać podział na wypadek lekki, ciężki, zbiorowy lub śmiertelny
//    // na potrzeby ostrzezenia dodać opcja szacowanego poziomu zagrożenia
//    @Builder.Default
//    @ManyToMany(fetch = FetchType.EAGER)
//    @JoinTable(
//            name = "safety_report_srt",
//            joinColumns = @JoinColumn(name = "safety_report_id"),
//            inverseJoinColumns = @JoinColumn(name = "srt_id"))
//    private Set<SafetyReportType> type = new LinkedHashSet<>();
//
//    // to posłuży do logiki czy jest nadal aktywne lub nie i ewentualnie inne opcje
//    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
//    @Column(columnDefinition = "safety_report_status", nullable = false)
//    private SafetyReportStatus status;
//
//    @ManyToOne(fetch = FetchType.EAGER)
//    @JoinColumn(name = "reported_by_employee_id", nullable = false, updatable = false)
//    private Employee reportedByEmployeeId;
//
//    @Column(nullable = false)
//    private int severityLevel = 1;
//
//    // opcja dodania wpisu anonimowo
//    private boolean secret;
// }
