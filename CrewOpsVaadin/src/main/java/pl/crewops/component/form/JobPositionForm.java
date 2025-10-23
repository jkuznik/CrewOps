package pl.crewops.component.form;

import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.textfield.TextField;

public class JobPositionForm extends FormLayout {

    private final TextField name = new TextField();

    public JobPositionForm() {
        setSizeFull();

        add(name);
    }
}
