package pl.crewops.domain.registration;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.UUID;
import pl.crewops.auth.RoleDTO;
import pl.crewops.dto.address.CreateAddressDTO;
import pl.crewops.dto.company.CreateCompanyDTO;
import pl.crewops.dto.employee.CreateEmployeeDTO;
import pl.crewops.dto.tenant.CreateTenantDTO;
import pl.crewops.registration.CreateCustomerCommand;

class RegistrationTestFactory {

    static final String COMPANY_NAME = "COMPANY_NAME";
    static final String CITY = "city";
    static final String FIRST_NAME = "firstName";

    static CreateTenantDTO createTenantDTO() {
        return CreateTenantDTO.builder()
                .createAddressDTO(CreateAddressDTO.builder()
                        .postalCode("postalCode")
                        .city(CITY)
                        .street("street")
                        .localNumber("localNumber")
                        .build())
                .createCompanyDTO(CreateCompanyDTO.builder()
                        .name(COMPANY_NAME)
                        .email("test@email.com")
                        .build())
                .build();
    }

    static CreateEmployeeDTO createEmployeeDTO() {
        return CreateEmployeeDTO.builder()
                .firstName(FIRST_NAME)
                .lastName("lastName")
                .department("department")
                .birthDate(LocalDate.now())
                .companyId(UUID.randomUUID())
                .username("username")
                .password("password")
                .phoneNumber("phoneNumber")
                .roles(new HashSet<RoleDTO>())
                .build();
    }

    static CreateCustomerCommand createCustomerCommand() {
        return CreateCustomerCommand.builder()
                .createTenantDTO(createTenantDTO())
                .createEmployeeDTO(createEmployeeDTO())
                .build();
    }
}
