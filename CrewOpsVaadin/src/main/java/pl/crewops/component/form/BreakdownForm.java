package pl.crewops.component.form;

import com.vaadin.flow.component.ComponentEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.binder.ValidationException;
import com.vaadin.flow.shared.Registration;
import java.util.UUID;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import pl.crewops.model.BreakdownFormModel;
import pl.crewops.model.dto.employee.EmployeeDTO;
import pl.crewops.security.custom.UserPrincipal;

public class BreakdownForm extends FormLayout {

    private final TextField machine = new TextField();
    private final TextArea description = new TextArea();
    private final Checkbox solved = new Checkbox();
    private final Checkbox critical = new Checkbox();

    private final Button save = new Button();
    private final Button update = new Button();
    private final Button close = new Button();

    private final Binder<BreakdownFormModel> binder = new BeanValidationBinder<>(BreakdownFormModel.class);

    public BreakdownForm() {
        addClassName("breakdown-form");

        localize();
        configDescriptionField();
        configureStaticBindings();
        add(machine, description, solved, critical, createButtonsLayout());
    }

    private void localize() {
        machine.setLabel(getTranslation("breakdownForm.machine"));
        description.setLabel(getTranslation("breakdownForm.description"));
        solved.setLabel(getTranslation("breakdownForm.solved"));
        critical.setLabel(getTranslation("breakdownForm.critical"));

        save.setText(getTranslation("breakdownForm.save"));
        update.setText(getTranslation("breakdownForm.update"));
        close.setText(getTranslation("breakdownForm.close"));
    }

    private void configDescriptionField() {
        description.setWidthFull();
        description.setMinHeight("100px");
        description.setMaxHeight("300px");
        description.getStyle().set("resize", "vertical");
    }

    private void configureStaticBindings() {
        binder.forField(machine).bindReadOnly(model -> model.getMachine().registerNumber());
    }

    private HorizontalLayout createButtonsLayout() {
        save.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        update.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        close.addThemeVariants(ButtonVariant.LUMO_TERTIARY);

        save.addClickShortcut(Key.ENTER);
        close.addClickShortcut(Key.ESCAPE);

        save.addClickListener(e -> validateAndSave());
        update.addClickListener(e -> validateAndUpdate());
        close.addClickListener(e -> fireEvent(new CloseEvent(this)));

        return new HorizontalLayout(save, update, close);
    }

    private void clearBinderBindings() {
        binder.removeBinding(description);
        binder.removeBinding(solved);
        binder.removeBinding(critical);
    }

    public void setFormModeSave() {
        clearBinderBindings();

        binder.forField(description)
                .asRequired(getTranslation("breakdownForm.description.required"))
                .withValidator(
                        desc -> desc.length() >= 5 && desc.length() <= 2047,
                        getTranslation("breakdownForm.description.length"))
                .bind(BreakdownFormModel::getDescription, BreakdownFormModel::setDescription);

        binder.forField(critical).bind(BreakdownFormModel::isCritical, BreakdownFormModel::setCritical);

        save.setVisible(true);
        update.setVisible(false);
        solved.setVisible(false);
        critical.setVisible(true);
        description.setReadOnly(false);
    }

    public void setFormModeUpdate() {
        clearBinderBindings();

        binder.forField(description).bind(BreakdownFormModel::getDescription, BreakdownFormModel::setDescription);

        binder.forField(solved).bind(BreakdownFormModel::isSolved, BreakdownFormModel::setSolved);

        update.setVisible(true);
        save.setVisible(false);
        solved.setVisible(true);
        solved.setReadOnly(false);
        critical.setVisible(false);
        description.setReadOnly(true);
    }

    public void setFormModeEmployeePermission() {
        setFormModeUpdate();
        solved.setReadOnly(true);
        update.setVisible(false);
    }

    private void validateAndSave() {
        BreakdownFormModel model = new BreakdownFormModel();
        try {
            binder.writeBean(model);
            model.setMachine(binder.getBean().getMachine());

            model.setReportedBy(EmployeeDTO.builder().id(getLoggedEmployeeId()).build());
            fireEvent(new SaveEvent(this, model));
        } catch (ValidationException e) {
        }
    }

    private UUID getLoggedEmployeeId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
        return userPrincipal.getEmployeeId();
    }

    private void validateAndUpdate() {
        if (!solved.getValue()) {
            solved.setInvalid(true);
            solved.setErrorMessage(getTranslation("breakdownForm.solved.required"));
            return;
        }

        try {
            BreakdownFormModel model = binder.getBean();
            model.setRepairedBy(EmployeeDTO.builder().id(getLoggedEmployeeId()).build());
            fireEvent(new UpdateEvent(this, model));
        } catch (Exception e) {
            Notification.show("Unexpected error while updating");
        }
    }

    public void setBreakdown(BreakdownFormModel breakdownFormModel) {
        binder.setBean(breakdownFormModel);
    }

    public abstract static class BreakdownFormEvent extends ComponentEvent<BreakdownForm> {
        private final BreakdownFormModel breakdownFormModel;

        protected BreakdownFormEvent(BreakdownForm source, BreakdownFormModel breakdownFormModel) {
            super(source, false);
            this.breakdownFormModel = breakdownFormModel;
        }

        public BreakdownFormModel getBreakdown() {
            return breakdownFormModel;
        }
    }

    public static class SaveEvent extends BreakdownFormEvent {
        SaveEvent(BreakdownForm source, BreakdownFormModel breakdownFormModel) {
            super(source, breakdownFormModel);
        }
    }

    public static class UpdateEvent extends BreakdownFormEvent {
        UpdateEvent(BreakdownForm source, BreakdownFormModel breakdownFormModel) {
            super(source, breakdownFormModel);
        }
    }

    public static class CloseEvent extends BreakdownFormEvent {
        CloseEvent(BreakdownForm source) {
            super(source, null);
        }
    }

    public Registration addSaveListener(ComponentEventListener<SaveEvent> listener) {
        return addListener(SaveEvent.class, listener);
    }

    public Registration addUpdateListener(ComponentEventListener<UpdateEvent> listener) {
        return addListener(UpdateEvent.class, listener);
    }

    public Registration addCloseListener(ComponentEventListener<CloseEvent> listener) {
        return addListener(CloseEvent.class, listener);
    }
}
