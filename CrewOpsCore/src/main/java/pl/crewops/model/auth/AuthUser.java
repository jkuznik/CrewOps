package pl.crewops.model.auth;

import jakarta.persistence.*;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.*;
import pl.crewops.dto.employee.EmployeeDTO;
import pl.crewops.dto.qualification.QualificationDTO;
import pl.crewops.dto.vehicle.VehicleDTO;
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

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "employee_id", referencedColumnName = "id", nullable = false, unique = true)
    private Employee employee;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "auth_user_role",
            joinColumns = @JoinColumn(name = "auth_user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id"))
    private Set<Role> roles;

    public EmployeeDTO exctractEmployeeDTO() {
        return EmployeeDTO.builder()
                .id(employee.getId())
                .firstName(employee.getFirstName())
                .lastName(employee.getLastName())
                .birthDate(employee.getBirthDate())
                .phoneNumber(employee.getPhoneNumber())
                .department(employee.getDepartment())
                .qualifications(employee.getQualifications().stream()
                        .map(qualification -> QualificationDTO.builder()
                                .id(qualification.getId())
                                .description(qualification.getDescription())
                                .build())
                        .collect(Collectors.toSet()))
                .vehicles(employee.getVehicles().stream()
                        .map(vehicle -> VehicleDTO.builder()
                                .id(vehicle.getId())
                                .make(vehicle.getMake())
                                .model(vehicle.getModel())
                                .year(vehicle.getYear())
                                //                                .vehicleType(vehicle.getVehicleType().toDTO())
                                .registerNumber(vehicle.getRegisterNumber())
                                .vin(vehicle.getVin())
                                .broken(vehicle.getBroken())
                                .build())
                        .collect(Collectors.toSet()))
                .build();
    }
}
