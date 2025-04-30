package pl.crewops.model.auth;

import jakarta.persistence.*;
import java.util.Set;
import lombok.*;
import pl.crewops.dto.employee.EmployeeDTO;
import pl.crewops.model.AbstractEntity;
import pl.crewops.model.Employee;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuthUser extends AbstractEntity {

    @Column(nullable = false, unique = true)
    private String username;

    @Column(nullable = false)
    private String password;

    // TODO: add logic to delete authUser in case delete employee
    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "employee_id", referencedColumnName = "id", nullable = false, unique = true)
    private Employee employee;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "auth_user_role",
            joinColumns = @JoinColumn(name = "auth_user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id"))
    private Set<Role> roles;

    // TODO: qualifications and vehicles set are skipped, add logic if necessary
    public EmployeeDTO exctractEmployeeDTO() {
        return EmployeeDTO.builder()
                .id(employee.getId())
                .firstName(employee.getFirstName())
                .lastName(employee.getLastName())
                .birthDate(employee.getBirthDate())
                .phoneNumber(employee.getPhoneNumber())
                .department(employee.getDepartment())
                .build();
    }
}
