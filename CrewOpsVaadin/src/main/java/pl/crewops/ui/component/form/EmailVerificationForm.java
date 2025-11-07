package pl.crewops.ui.component.form;

import com.vaadin.flow.component.ComponentEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.NumberField;
import com.vaadin.flow.shared.Registration;
import lombok.Getter;

public class EmailVerificationForm extends FormLayout {

    private final Span infoText = new Span();
    private final NumberField verificationCode = new NumberField();
    private final Button verify = new Button();
    private final Button cancel = new Button();

    public EmailVerificationForm() {
        addClassName("email-verification-form");

        localize();

        configInfoText();
        configVerifyButton();
        configCancelButton();

        var verticalLayout = new VerticalLayout(infoText, verificationCode, verify, cancel);
        verticalLayout.setSizeUndefined();
        verticalLayout.setSpacing(true);
        verticalLayout.setPadding(false);

        add(verticalLayout);
    }

    private void localize() {
        infoText.setText(getTranslation("emailVerificationForm.infoText"));
        verificationCode.setPlaceholder(getTranslation("emailVerificationForm.verificationCode.placeholder"));
        verify.setText(getTranslation("emailVerificationForm.verify.button"));
        cancel.setText(getTranslation("emailVerificationForm.cancel.button"));
    }

    private void configInfoText() {
        infoText.getStyle()
                .set("font-size", "var(--lumo-font-size-s)")
                .set("color", "var(--lumo-secondary-text-color)")
                .set("max-width", "400px") // or any width that fits your design
                .set("white-space", "normal");

        verificationCode.setWidthFull();
    }

    private void configVerifyButton() {
        verify.addThemeVariants(ButtonVariant.LUMO_SUCCESS);
        verify.setWidthFull();
        verify.addClickListener(event -> {
            fireEvent(new VerifyEmailEvent(this, verificationCode.getValue().intValue()));
        });
    }

    private void configCancelButton() {
        cancel.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        cancel.setWidthFull();
        cancel.addClickListener(event -> {
            fireEvent(new CancelEvent(this));
        });
    }

    public abstract static class EmailVerificationFormEvent extends ComponentEvent<EmailVerificationForm> {

        public EmailVerificationFormEvent(EmailVerificationForm source) {
            super(source, false);
        }
    }

    public static class VerifyEmailEvent extends EmailVerificationFormEvent {

        @Getter
        private final int verificationCode;

        public VerifyEmailEvent(EmailVerificationForm source, int verificationCode) {
            super(source);
            this.verificationCode = verificationCode;
        }
    }

    public static class CancelEvent extends EmailVerificationFormEvent {
        public CancelEvent(EmailVerificationForm source) {
            super(source);
        }
    }

    public Registration addVerifyEmailListener(ComponentEventListener<VerifyEmailEvent> listener) {
        return addListener(VerifyEmailEvent.class, listener);
    }

    public Registration addCancelEmailListener(ComponentEventListener<CancelEvent> listener) {
        return addListener(CancelEvent.class, listener);
    }
}
