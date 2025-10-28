package pl.crewops.domain.registration;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import pl.crewops.model.dto.address.CreateAddressDTO;
import pl.crewops.model.dto.company.CreateCompanyDTO;
import pl.crewops.model.dto.department.DepartmentDTO;
import pl.crewops.model.dto.employee.CreateEmployeeDTO;
import pl.crewops.model.dto.registration.CreateCustomerCommand;
import pl.crewops.model.dto.tenant.CreateTenantDTO;

class RegistrationTestFactory {

    static final String COMPANY_NAME = "TestCompanyName";
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
                        .email("company@email.com")
                        .taxId("testTaxId2")
                        .build())
                .build();
    }

    static CreateTenantDTO createTenantDTOWithAlreadyExistTenantValues() {
        return CreateTenantDTO.builder()
                .createAddressDTO(CreateAddressDTO.builder()
                        .postalCode("postalCode")
                        .city(CITY)
                        .street("street")
                        .localNumber("localNumber")
                        .build())
                .createCompanyDTO(CreateCompanyDTO.builder()
                        .name(COMPANY_NAME)
                        .email("company@email.com")
                        .taxId("test_tax_id")
                        .build())
                .build();
    }

    static CreateEmployeeDTO createEmployeeDTO() {
        return CreateEmployeeDTO.builder()
                .firstName(FIRST_NAME)
                .lastName("lastName")
                .departments(departmentsDTOs())
                .companyId(UUID.randomUUID())
                .phoneNumber("phoneNumber")
                .roles(new HashSet<>())
                .build();
    }

    static CreateCustomerCommand createCustomerCommand() {
        return CreateCustomerCommand.builder()
                .createTenantDTO(createTenantDTO())
                .createEmployeeDTO(createEmployeeDTO())
                .build();
    }

    static CreateCustomerCommand createCustomerCommandThatBreakUniqueConstraints() {
        return CreateCustomerCommand.builder()
                .createTenantDTO(createTenantDTOWithAlreadyExistTenantValues())
                .createEmployeeDTO(createEmployeeDTO())
                .build();
    }

    static Set<DepartmentDTO> departmentsDTOs() {
        return Set.of(DepartmentDTO.builder().name("department").build());
    }
}
