package pl.crewops.ui.component.custom.schedule;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import pl.crewops.enums.ScheduleTemplateType;
import pl.crewops.exceptions.NotAuthenticatedException;
import pl.crewops.infrastructure.core.CoreAPI;
import pl.crewops.model.dto.scheduleTemplate.CreateScheduleTemplateDTO;
import pl.crewops.ui.component.notification.FailNotification;
import pl.crewops.ui.component.notification.SuccessNotification;
import pl.crewops.util.AuthenticationResolver;
import pl.crewops.util.SpringContextBridge;

public class ScheduleTemplateForm extends FormLayout {

    private final CoreAPI coreAPI;
    private final AuthenticationResolver authenticationResolver;
    private final NativeScheduleGrid nativeGrid;

    private final TextField scheduleName = new TextField();
    private final Button save = new Button(getTranslation("saveButton"));

    private final Binder<TemplateFormModel> binder = new Binder<>(TemplateFormModel.class);

    public ScheduleTemplateForm(NativeScheduleGrid nativeScheduleGrid) {
        this.coreAPI = SpringContextBridge.getBean(CoreAPI.class);
        this.authenticationResolver = SpringContextBridge.getBean(AuthenticationResolver.class);
        this.nativeGrid = nativeScheduleGrid;

        configureFields();
        configureBinder();
        configureButtons();

        add(scheduleName, save);
    }

    private void configureFields() {
        scheduleName.setWidthFull();
        scheduleName.setPlaceholder(getTranslation("scheduleTemplateForm.placeholder"));
    }

    private void configureBinder() {
        binder.forField(scheduleName)
                .asRequired(getTranslation("validation.required"))
                //                .withValidator(name -> name.length() >= 3, getTranslation("scheduleName.tooShort"))
                .bind(TemplateFormModel::getName, TemplateFormModel::setName);

        // Ustawiamy początkowy pusty obiekt
        binder.setBean(new TemplateFormModel());
    }

    private void configureButtons() {
        save.setWidthFull();
        save.addThemeVariants(ButtonVariant.LUMO_SUCCESS);
        save.addClickListener(event -> {
            if (binder.validate().isOk()) { // Binder sprawdza wszystkie reguły
                TemplateFormModel model = binder.getBean();

                CreateScheduleTemplateDTO createDto = CreateScheduleTemplateDTO.builder()
                        .name(model.getName())
                        .authorEmployeeId(authenticationResolver.getPrincipal().getEmployeeId())
                        .type(ScheduleTemplateType.DAILY)
                        .privateOwner(false)
                        .days(nativeGrid.collectDataFromGrid())
                        .build();

                try {
                    coreAPI.createSchedule(createDto).ifPresent(s -> {
                        // todo
                        new SuccessNotification("todo");
                        binder.setBean(new TemplateFormModel()); // Reset formularza
                    });
                } catch (NotAuthenticatedException e) {
                    new FailNotification("todo");
                }
            }
        });
    }

    public static class TemplateFormModel {
        private String name = "";

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }
}
