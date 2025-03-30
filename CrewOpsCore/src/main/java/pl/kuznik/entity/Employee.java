package pl.kuznik.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;
import java.util.LinkedHashSet;
import java.util.Set;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "employee")
public class Employee extends AbstractEntity {

    @Size(max = 31)
    @NotNull
    @Column(updatable = false)
    private String firstName;

    @Size(max = 31)
    @NotNull
    @Column(updatable = false)
    private String lastName;

    @NotNull
    @Column(updatable = false)
    private LocalDate birthDate;

    @Size(max = 15)
    private String phoneNumber;

    @Size(max = 31)
    @NotNull
    private String department;

    @Builder.Default
    @ManyToMany(cascade = CascadeType.PERSIST)
    @JoinTable(
            name = "employee_qualification",
            joinColumns = @JoinColumn(name = "employee_id"),
            inverseJoinColumns = @JoinColumn(name = "qualification_id"))
    private Set<Qualification> qualifications = new LinkedHashSet<>();

    @Builder.Default
    @ManyToMany(cascade = CascadeType.PERSIST)
    @JoinTable(
            name = "employee_vehicle",
            joinColumns = @JoinColumn(name = "employee_id"),
            inverseJoinColumns = @JoinColumn(name = "vehicle_id"))
    private Set<Vehicle> vehicles = new LinkedHashSet<>();
}
