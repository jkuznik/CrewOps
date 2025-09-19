package pl.crewops.domain.registration;

import pl.crewops.model.dto.registration.CreateRegistrationDTO;
import pl.crewops.model.dto.registration.RegistrationDTO;
import pl.crewops.model.publicSchema.Registration;

class RegistrationMapper {

    Registration mapToEntity(CreateRegistrationDTO dto) {
        return Registration.builder()
                .status(dto.status())
                .verificationCode(dto.verificationCode())
                .companyName(dto.companyName())
                .taxId(dto.taxId())
                .email(dto.email())
                .postalCode(dto.postalCode())
                .city(dto.city())
                .street(dto.street())
                .localNumber(dto.localNumber())
                .firstName(dto.firstName())
                .lastName(dto.lastName())
                .birthDate(dto.birthDate())
                .phoneNumber(dto.phoneNumber())
                .build();
    }

    public static RegistrationDTO toDTO(Registration registration) {
        return new RegistrationDTO(
                registration.getId(),
                registration.getCreatedAt(),
                registration.getStatus(),
                registration.getVerificationCode(),
                registration.getCompanyName(),
                registration.getTaxId(),
                registration.getEmail(),
                registration.getPostalCode(),
                registration.getCity(),
                registration.getStreet(),
                registration.getLocalNumber(),
                registration.getFirstName(),
                registration.getLastName(),
                registration.getBirthDate(),
                registration.getPhoneNumber());
    }
}
