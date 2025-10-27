package pl.crewops.ui.component.custom;

import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.dependency.CssImport;

@CssImport("./styles/component/combo-box.css")
public class ComboBoxCustom<T> extends ComboBox<T> {

    private static final String CUSTOM_CLASS_NAME = "crewops-default-combobox";
    private static final String CUSTOM_THEME_NAME = "crewops-default-combo";

    public ComboBoxCustom() {
        addClassName(CUSTOM_CLASS_NAME);

        getElement().setAttribute("theme", CUSTOM_THEME_NAME);
    }
}
