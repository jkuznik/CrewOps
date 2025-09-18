package pl.crewops.model.dto.registration;

import java.time.Instant;
import java.util.UUID;
import pl.crewops.enums.RegistrationStatus;

public record RegistrationDTO(
        UUID id,
        Instant createdAt,
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
