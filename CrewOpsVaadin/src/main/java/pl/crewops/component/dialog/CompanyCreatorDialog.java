package pl.crewops.component.dialog;

import com.vaadin.flow.component.dialog.Dialog;
import java.util.Set;
import java.util.UUID;
import pl.crewops.component.form.CompanyCreatorForm;
import pl.crewops.dto.address.CreateAddressDTO;
import pl.crewops.dto.auth.RoleDTO;
import pl.crewops.dto.company.CreateCompanyDTO;
import pl.crewops.dto.employee.CreateEmployeeDTO;
import pl.crewops.dto.tenant.CreateTenantDTO;
import pl.crewops.exceptions.NotAuthenticatedException;
import pl.crewops.infrastructure.core.CoreAPI;
import pl.crewops.model.EmployeeFormModel;
import pl.crewops.model.auth.RoleType;
import pl.crewops.registration.CreateCustomerCommand;

public class CompanyCreatorDialog extends Dialog {

    public CompanyCreatorDialog(CoreAPI coreAPI) {
        addClassName("company-creator-notification");

        var companyCreatorForm = new CompanyCreatorForm();
        companyCreatorForm.addSaveButtonListener(companyInformation -> createNewTenant(coreAPI, companyInformation));
        companyCreatorForm.addCloseButtonListener(event -> close());

        add(companyCreatorForm);
        open();
    }

    private void createNewTenant(CoreAPI coreAPI, CompanyInformation companyInformation) {
        var createTenantDTO = getCreateTenantDTO(companyInformation);
        var createEmployeeDTO = CreateEmployeeDTO.builder()
                // TODO: to fix this is required remove constraint or create brand new object for FE
                //  or just accept as it is
                .companyId(UUID.randomUUID())
                // This companyId value is set only to satisfy constraints validations.
                // Proper value of companyId is set on BE side after dynamic generated
                // value when new record is saved in Company table in persist layer.
                .firstName(companyInformation.initialEmployeeInfo.getFirstName())
                .lastName(companyInformation.initialEmployeeInfo.getLastName())
                .department(companyInformation.initialEmployeeInfo.getDepartment())
                .phoneNumber(companyInformation.initialEmployeeInfo.getPhoneNumber())
                .birthDate(companyInformation.initialEmployeeInfo.getBirthDate())
                .roles(companyAdminRoles())
                .build();
        var createCustomerCommand = CreateCustomerCommand.builder()
                .createTenantDTO(createTenantDTO)
                .createEmployeeDTO(createEmployeeDTO)
                .build();
        try {
            coreAPI.registerNewCustomer(createCustomerCommand);
        } catch (NotAuthenticatedException e) {
            System.out.println("Error creating new customer with initial employee");
        }
    }

    private Set<RoleDTO> companyAdminRoles() {
        return Set.of(
                RoleDTO.builder().name(RoleType.COMPANY_ADMIN.name()).build(),
                RoleDTO.builder().name(RoleType.EMPLOYEE.name()).build());
    }

    private CreateTenantDTO getCreateTenantDTO(CompanyInformation companyInformation) {
        return CreateTenantDTO.builder()
                .createCompanyDTO(CreateCompanyDTO.builder()
                        .name(companyInformation.companyName)
                        .email(companyInformation.companyEmail)
                        .taxId(companyInformation.companyTaxId)
                        .build())
                .createAddressDTO(CreateAddressDTO.builder()
                        .postalCode(companyInformation.postalCode)
                        .city(companyInformation.city)
                        .street(companyInformation.street)
                        .localNumber(companyInformation.localNumber)
                        .build())
                .build();
    }

    public record CompanyInformation(
            String companyName,
            String companyEmail,
            String companyTaxId,
            String postalCode,
            String city,
            String street,
            String localNumber,
            EmployeeFormModel initialEmployeeInfo) {}
}
