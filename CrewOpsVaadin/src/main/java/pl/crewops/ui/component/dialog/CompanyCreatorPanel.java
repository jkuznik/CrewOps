package pl.crewops.ui.component.dialog;

import static pl.crewops.model.dto.registration.PreRegisterResponse.PreRegisterResponseCode.*;

import com.vaadin.flow.shared.Registration;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import pl.crewops.infrastructure.core.CoreAPI;
import pl.crewops.model.EmployeeFormModel;
import pl.crewops.model.auth.RoleType;
import pl.crewops.model.dto.address.CreateAddressDTO;
import pl.crewops.model.dto.auth.RoleDTO;
import pl.crewops.model.dto.company.CreateCompanyDTO;
import pl.crewops.model.dto.employee.CreateEmployeeDTO;
import pl.crewops.model.dto.registration.CreateCustomerCommand;
import pl.crewops.model.dto.registration.CreateCustomerResult;
import pl.crewops.model.dto.registration.PreRegisterResponse;
import pl.crewops.model.dto.registration.VerifyEmailRequest;
import pl.crewops.model.dto.tenant.CreateTenantDTO;
import pl.crewops.ui.component.custom.PanelCustom;
import pl.crewops.ui.component.form.CompanyCreatorForm;
import pl.crewops.ui.component.form.EmailVerificationForm;
import pl.crewops.ui.component.form.EmployeeForm;
import pl.crewops.ui.component.notification.FailNotification;
import pl.crewops.ui.component.notification.SuccessNotification;
import pl.crewops.util.SpringContextBridge;

public class CompanyCreatorPanel extends PanelCustom {

    private static final String INDIVIDUAL = "Individual Customer";

    private final CompanyCreatorForm companyCreatorForm = new CompanyCreatorForm();
    private final EmployeeForm employeeForm = new EmployeeForm();
    private final EmailVerificationForm emailVerificationForm = new EmailVerificationForm();

    private Registration createTenantRegistration;

    public CompanyCreatorPanel() {
        addClassName("company-creator-notification");
        emailVerificationForm.setVisible(false);

        configureEmployeeForm();

        addContent(companyCreatorForm, employeeForm, emailVerificationForm);
    }

    private void createNewTenant(CompanyInformation companyInformation) {
        var coreAPI = SpringContextBridge.getBean(CoreAPI.class);
        var createCustomerCommand = getCreateCustomerCommand(companyInformation);

        try {
            Optional<PreRegisterResponse> preRegisterResponse = coreAPI.registerNewCustomer(createCustomerCommand);
            if (preRegisterResponse.isPresent()
                    && preRegisterResponse.get().code().equals(EMAIL_VERIFICATION_REQUIRED)) {

                companyCreatorForm.setVisible(false);
                employeeForm.setVisible(false);
                emailVerificationForm.setVisible(true);

                String subject =
                        getTranslation("companyCreatorDialog.successSubject") + " " + companyInformation.companyName;
                String bodyTemplate = getTranslation("companyCreatorDialog.successBody");

                emailVerificationForm.addVerifyEmailListener(event -> {
                    Optional<CreateCustomerResult> createCustomerResult = coreAPI.verifyEmail(new VerifyEmailRequest(
                            preRegisterResponse.get().registrationId(),
                            event.getVerificationCode(),
                            subject,
                            bodyTemplate));

                    createCustomerResult.ifPresent(customerResult -> {
                        this.setVisible(false);
                        new SuccessNotification(getTranslation("companyCreatorDialog.successNotification")
                                + " "
                                + customerResult.companyDTO().name());
                    });
                });

                emailVerificationForm.addCancelEmailListener(event -> {
                    emailVerificationForm.setVisible(false);
                    this.setVisible(false);
                });

            } else {
                new FailNotification(getTranslation("failNotification"));
                System.out.println("Error creating new customer with initial employee");
            }
        } catch (Exception e) {
            new FailNotification(getTranslation("failNotification"));
            System.out.println("Error creating new customer with initial employee");
        }
    }

    private void configureEmployeeForm() {
        employeeForm.setFormModeSave();

        createTenantRegistration = employeeForm.addSaveListener(saveEvent -> {
            if (companyCreatorForm.validate()) {
                Optional<CompanyInformation> companyInformation = companyCreatorForm.getCompanyInformation();
                CompanyInformation newTenant = companyInformation.orElseThrow(
                        () -> new IllegalArgumentException("Company information is null"));
                newTenant.setInitialEmployeeInfo(saveEvent.getEmployee());
                createNewTenant(newTenant);
            }
        });
        employeeForm.addCloseListener(event -> {
            this.setVisible(false);
        });
    }

    public void setCompanyRegistrationMode() {
        if (createTenantRegistration != null) {
            createTenantRegistration.remove();
        }

        this.setVisible(true);
        emailVerificationForm.setVisible(false);

        companyCreatorForm.setVisible(true);
        employeeForm.setVisible(true);
        employeeForm.setEmailRequired(false);

        createTenantRegistration = employeeForm.addSaveListener(saveEvent -> {
            if (companyCreatorForm.validate()) {
                Optional<CompanyInformation> companyInformation = companyCreatorForm.getCompanyInformation();
                CompanyInformation newTenant = companyInformation.orElseThrow(
                        () -> new IllegalArgumentException("Company information is null"));
                newTenant.setInitialEmployeeInfo(saveEvent.getEmployee());
                createNewTenant(newTenant);
            }
        });
    }

    public void setIndividualRegistrationMode() {
        if (createTenantRegistration != null) {
            createTenantRegistration.remove();
        }

        this.setVisible(true);
        emailVerificationForm.setVisible(false);

        companyCreatorForm.setVisible(false);
        employeeForm.setVisible(true);
        employeeForm.setEmailRequired(true);

        createTenantRegistration = employeeForm.addSaveListener(saveEvent -> {
            var newTenant = CompanyInformation.builder()
                    .companyName(INDIVIDUAL)
                    .companyEmail(saveEvent.getEmployee().getEmail())
                    .companyTaxId(INDIVIDUAL)
                    .street(INDIVIDUAL)
                    .postalCode(INDIVIDUAL)
                    .localNumber(INDIVIDUAL)
                    .city(INDIVIDUAL)
                    .initialEmployeeInfo(saveEvent.getEmployee())
                    .build();

            createNewTenant(newTenant);
        });
    }

    private CreateCustomerCommand getCreateCustomerCommand(CompanyInformation companyInformation) {
        var createTenantDTO = getCreateTenantDTO(companyInformation);
        var createEmployeeDTO = getCreateEmployeeDTO(companyInformation);

        return CreateCustomerCommand.builder()
                .createTenantDTO(createTenantDTO)
                .createEmployeeDTO(createEmployeeDTO)
                .build();
    }

    private CreateTenantDTO getCreateTenantDTO(CompanyInformation companyInformation) {
        return CreateTenantDTO.builder()
                .createCompanyDTO(CreateCompanyDTO.builder()
                        .name(companyInformation.getCompanyName())
                        .email(companyInformation.getCompanyEmail())
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

    private CreateEmployeeDTO getCreateEmployeeDTO(CompanyInformation companyInformation) {
        return CreateEmployeeDTO.builder()
                // TODO: to fix this is required remove constraint or create brand new object for FE
                //  or just accept as it is
                .companyId(UUID.randomUUID())
                // This companyId value is set only to satisfy constraints validations.
                // Proper value of companyId is set on BE side after dynamic generated
                // value when new record is saved in Company table in persist layer.
                .firstName(companyInformation.initialEmployeeInfo.getFirstName())
                .lastName(companyInformation.initialEmployeeInfo.getLastName())
                .phoneNumber(companyInformation.initialEmployeeInfo.getPhoneNumber())
                .roles(companyAdminRoles())
                .build();
    }

    private Set<RoleDTO> companyAdminRoles() {
        return Set.of(
                RoleDTO.builder().name(RoleType.COMPANY_ADMIN.name()).build(),
                RoleDTO.builder().name(RoleType.EMPLOYEE.name()).build());
    }

    @Getter
    @Setter
    @Builder
    public static class CompanyInformation {
        @NotNull
        @NotBlank
        @Size(min = 1, max = 63, message = "Company name must be between 1 and 63 characters")
        private String companyName;

        @NotNull
        @Email
        private String companyEmail;

        @NotNull
        @NotBlank
        private String companyTaxId;

        @NotNull
        private String postalCode;

        @NotNull
        private String city;

        @NotNull
        private String street;

        @NotNull
        private String localNumber;

        @NotNull
        private EmployeeFormModel initialEmployeeInfo;
    }
}
