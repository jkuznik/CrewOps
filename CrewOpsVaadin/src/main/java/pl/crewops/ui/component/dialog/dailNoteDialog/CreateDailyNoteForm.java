package pl.crewops.ui.component.dialog.dailNoteDialog;
// pl.crewops.ui.component.dialog.dailNoteDialog.CreateDailyNoteForm.java

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextArea;

// KLUCZOWA ZMIANA: Z FormLayout na VerticalLayout
class CreateDailyNoteForm extends VerticalLayout {

    private final Span optionDescritpion = new Span("text");
    private final Checkbox dailyEntryRelated = new Checkbox();
    private final TextArea noteTextArea = new TextArea();
    private final Button addButton = new Button("add");

    public CreateDailyNoteForm() {
        setSizeFull();
        setPadding(false);

        var option = new HorizontalLayout(optionDescritpion, dailyEntryRelated);
        option.setAlignItems(FlexComponent.Alignment.CENTER);
        option.setWidthFull();

        noteTextArea.setSizeFull();

        var buttonContainer = new HorizontalLayout(addButton);
        buttonContainer.setWidthFull();
        buttonContainer.setJustifyContentMode(FlexComponent.JustifyContentMode.START);

        // Dodajemy komponenty bezpośrednio do VerticalLayout (samego formularza)
        add(noteTextArea, option, buttonContainer);

        // Ponieważ jest to VerticalLayout, to właśnie jego dzieci powinny rosnąć.
        // KLUCZOWA LINIA: Mówimy, że noteTextArea ma zająć całą wolną przestrzeń w pionie.
        setFlexGrow(1, noteTextArea);
    }
}
