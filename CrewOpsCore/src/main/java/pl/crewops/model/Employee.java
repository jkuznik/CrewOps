package pl.crewops.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;
import java.util.LinkedHashSet;
import java.util.Set;
import lombok.*;
import pl.crewops.model.dto.employee.CreateEmployeeDTO;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Employee extends AbstractEntity {

    @Size(max = 31)
    @NotNull
    @Column(updatable = false)
    private String firstName;

    @Size(max = 31)
    @NotNull
    @Column(updatable = false)
    private String lastName;

    // todo: birthDate should be updatable
    @NotNull
    @Column(updatable = false)
    private LocalDate birthDate;

    @Size(max = 15)
    private String phoneNumber;

    private boolean active;

    @Builder.Default
    @ManyToMany(cascade = CascadeType.PERSIST, fetch = FetchType.EAGER)
    @JoinTable(
            name = "employee_department",
            joinColumns = @JoinColumn(name = "employee_id"),
            inverseJoinColumns = @JoinColumn(name = "department_id"))
    private Set<Department> departments = new LinkedHashSet<>();

    @Builder.Default
    @ManyToMany(cascade = CascadeType.PERSIST, fetch = FetchType.EAGER)
    @JoinTable(
            name = "employee_qualification",
            joinColumns = @JoinColumn(name = "employee_id"),
            inverseJoinColumns = @JoinColumn(name = "qualification_id"))
    private Set<Qualification> qualifications = new LinkedHashSet<>();

    @Builder.Default
    @ManyToMany(cascade = CascadeType.PERSIST, fetch = FetchType.EAGER)
    @JoinTable(
            name = "employee_machine",
            joinColumns = @JoinColumn(name = "employee_id"),
            inverseJoinColumns = @JoinColumn(name = "machine_id"))
    private Set<Machine> machines = new LinkedHashSet<>();

    public Employee mapToEntity(CreateEmployeeDTO createEmployeeDTO) {
        return Employee.builder()
                .firstName(createEmployeeDTO.firstName())
                .lastName(createEmployeeDTO.lastName())
                .birthDate(createEmployeeDTO.birthDate())
                .phoneNumber(createEmployeeDTO.phoneNumber())
                .build();
    }
}
