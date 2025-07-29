package pl.crewops.view.component.notification.formNotification;

import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.textfield.TextField;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.function.Consumer;
import pl.crewops.dto.address.CreateAddressDTO;
import pl.crewops.dto.auth.RoleDTO;
import pl.crewops.dto.company.CreateCompanyDTO;
import pl.crewops.dto.employee.CreateEmployeeDTO;
import pl.crewops.dto.tenant.CreateTenantDTO;
import pl.crewops.exceptions.NotAuthenticatedException;
import pl.crewops.infrastructure.core.CoreAPI;
import pl.crewops.model.EmployeeFormModel;
import pl.crewops.registration.CreateCustomerCommand;
import pl.crewops.view.component.form.EmployeeForm;

public class CompanyCreatorNotification extends Notification {

    public CompanyCreatorNotification(CoreAPI coreAPI) {
        addClassName("company-creator-notification");
        open();
        addThemeVariants(NotificationVariant.LUMO_SUCCESS);
        setPosition(Notification.Position.MIDDLE);
        setDuration(0);

        var companyCreatorForm = new CompanyCreatorForm();

        companyCreatorForm.addSaveButtonListener(companyInformation -> createNewTenant(coreAPI, companyInformation));
        companyCreatorForm.addCloseButtonListener(event -> close());
        add(companyCreatorForm);
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
                .roles(extractRoles(companyInformation))
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

    private Set<RoleDTO> extractRoles(CompanyInformation companyInformation) {
        var roles = new HashSet<RoleDTO>();
        companyInformation.initialEmployeeInfo.getRoles().forEach(role -> {
            roles.add(new RoleDTO(role.name()));
        });

        return roles;
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

    private static class CompanyCreatorForm extends FormLayout {
        // company
        private final TextField companyName = new TextField();
        private final TextField companyEmail = new TextField();
        private final TextField companyTaxId = new TextField();
        // address
        private final TextField postalCode = new TextField();
        private final TextField city = new TextField();
        private final TextField street = new TextField();
        private final TextField localNumber = new TextField();
        // employee
        private final EmployeeForm employeeForm = new EmployeeForm();

        private Consumer<CompanyInformation> saveButtonListener;
        private Consumer<Void> closeButtonListener;

        public CompanyCreatorForm() {
            addClassName("company-creator-form");

            localize();

            employeeForm.setFormModeSave();
            employeeForm.addSaveListener(this::saveEmployee);
            employeeForm.addCloseListener(this::closeButton);
            add(companyName, companyEmail, companyTaxId, postalCode, city, street, localNumber, employeeForm);
        }

        private void localize() {
            companyName.setLabel(getTranslation("companyCreatorForm.companyName"));
            companyEmail.setLabel(getTranslation("companyCreatorForm.companyEmail"));
            companyTaxId.setLabel(getTranslation("TODOTODOTODOTODOTODO"));
            postalCode.setLabel(getTranslation("companyCreatorForm.postalCode"));
            city.setLabel(getTranslation("companyCreatorForm.city"));
            street.setLabel(getTranslation("companyCreatorForm.street"));
            localNumber.setLabel(getTranslation("companyCreatorForm.localNumber"));
        }

        private void saveEmployee(EmployeeForm.SaveEvent event) {
            if (saveButtonListener != null) {

                saveButtonListener.accept(new CompanyInformation(
                        companyName.getValue(),
                        companyEmail.getValue(),
                        companyTaxId.getValue(),
                        postalCode.getValue(),
                        city.getValue(),
                        street.getValue(),
                        localNumber.getValue(),
                        event.getEmployee()));
            }
        }

        private void closeButton(EmployeeForm.CloseEvent event) {
            if (closeButtonListener != null) {
                closeButtonListener.accept(null);
            }
        }

        public void addCloseButtonListener(Consumer<Void> closeButtonListener) {
            this.closeButtonListener = closeButtonListener;
        }

        public void addSaveButtonListener(Consumer<CompanyInformation> listener) {
            this.saveButtonListener = listener;
        }
    }

    private record CompanyInformation(
            String companyName,
            String companyEmail,
            String companyTaxId,
            String postalCode,
            String city,
            String street,
            String localNumber,
            EmployeeFormModel initialEmployeeInfo) {}
}
