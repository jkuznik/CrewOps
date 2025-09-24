package pl.crewops.model;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;
import java.util.UUID;
import lombok.*;
import pl.crewops.model.dto.employee.EmployeeDTO;
import pl.crewops.security.custom.UserPrincipal;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProfileFormModel {
    private UUID employeeId;
    private String firstName;
    private String lastName;
    private LocalDate birthDate;
    private @Size(max = 15) String phoneNumber;
    private @Email String email;
    private String username;
    private String password;

    public static ProfileFormModel create(UserPrincipal principal, EmployeeDTO employeeDTO) {
        return ProfileFormModel.builder()
                .employeeId(principal.getEmployeeId())
                .firstName(employeeDTO.firstName())
                .lastName(employeeDTO.lastName())
                .birthDate(employeeDTO.birthDate())
                .phoneNumber(employeeDTO.phoneNumber())
                .email(employeeDTO.email())
                .username(principal.getUsername())
                .build();
    }
}
