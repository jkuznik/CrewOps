package pl.crewops.model.dto.registration;

import java.time.Instant;
import lombok.Builder;
import pl.crewops.enums.RegistrationStatus;

@Builder
public record CreateRegistrationDTO(
        RegistrationStatus status,
        int verificationCode,
        // company info
        String companyName,
        String taxId,
        String email,
        // company address info
        String postalCode,
        String city,
        String street,
        String localNumber,
        // employee info
        String firstName,
        String lastName,
        Instant birthDate,
        String phoneNumber) {}
