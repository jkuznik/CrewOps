package pl.crewops.ui.component.dialog.dailNoteDialog;

import com.vaadin.flow.component.ComponentEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.radiobutton.RadioButtonGroup;
import com.vaadin.flow.component.radiobutton.RadioGroupVariant;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.shared.Registration;
import lombok.Getter;

class CreateDailyNoteForm extends HorizontalLayout {

    private final TextArea noteTextArea = new TextArea();
    private final RadioButtonGroup<Boolean> noteType = new RadioButtonGroup<>();
    private final Button addButton = new Button();

    private final Span todo = new Span("Zaimplementować opcję 'Przypomninacza'");

    public CreateDailyNoteForm() {
        setSizeFull();
        setPadding(false);

        localize();

        noteTextArea.setSizeFull();

        var configuredCheckboxAndButtonContainer = configuredCheckboxAndButtonContainer();

        add(noteTextArea, configuredCheckboxAndButtonContainer);
    }

    private VerticalLayout configuredCheckboxAndButtonContainer() {
        var configuredCheckboxAndButtonContainer = new VerticalLayout();
        configuredCheckboxAndButtonContainer.setSpacing(true);
        configuredCheckboxAndButtonContainer.setPadding(true);
        configuredCheckboxAndButtonContainer.setAlignItems(Alignment.END);
        configuredCheckboxAndButtonContainer.add(todo, noteType, addButton);

        noteType.setItems(true, false);
        noteType.setItemLabelGenerator(booleanItem -> {
            if (booleanItem) {
                return getTranslation("dailyActivityForm.noteTypePrivate");
            } else {
                return getTranslation("dailyActivityForm.noteTypePublic");
            }
        });
        noteType.addThemeVariants(RadioGroupVariant.LUMO_VERTICAL);

        noteType.addValueChangeListener(event -> {
            addButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
            addButton.setEnabled(true);
        });

        addButton.setEnabled(false);
        addButton.addClickListener(event -> {
            fireEvent(new AddNoteEvent(this, noteTextArea.getValue(), noteType.getValue()));
        });
        return configuredCheckboxAndButtonContainer;
    }

    private void localize() {
        noteType.setLabel(getTranslation("dailyActivityForm.noteType"));
        addButton.setText(getTranslation("dailyActivityForm.addNote"));
    }

    public abstract static class CreateDailyNoteFormEvent extends ComponentEvent<CreateDailyNoteForm> {
        public CreateDailyNoteFormEvent(CreateDailyNoteForm source) {
            super(source, false);
        }
    }

    @Getter
    public static class AddNoteEvent extends CreateDailyNoteFormEvent {

        private final String content;
        private final boolean privateNote;

        public AddNoteEvent(CreateDailyNoteForm source, String content, boolean privateNote) {
            super(source);
            this.content = content;
            this.privateNote = privateNote;
        }
    }

    public Registration addCreateNoteListener(ComponentEventListener<AddNoteEvent> listener) {
        return addListener(AddNoteEvent.class, listener);
    }
}
