package pl.crewops.view.component.navbarComponents;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.i18n.LocaleChangeEvent;
import com.vaadin.flow.i18n.LocaleChangeObserver;
import java.util.Locale;
import java.util.Map;
import pl.crewops.infrastructure.localization.CustomI18NProvider;

@CssImport("./styles/component/language-selector.css")
public class LanguageSelectorComponent extends HorizontalLayout implements LocaleChangeObserver {

    private final Map<String, Locale> options;
    private final ComboBox<String> select = new ComboBox<>();

    public LanguageSelectorComponent() {
        options = CustomI18NProvider.getFlagToLocale();

        configureComboBox();

        add(select);
    }

    private void configureComboBox() {
        select.setClassName("flag-combo");
        select.setThemeName("language-combo");
        select.setItems(options.keySet());
        select.setLabel(null);

        if (UI.getCurrent() != null) {
            Locale currentLocale = UI.getCurrent().getLocale();

            String currentFlag = options.entrySet().stream()
                    .filter(entry -> entry.getValue().getLanguage().equals(currentLocale.getLanguage()))
                    .map(Map.Entry::getKey)
                    .findFirst()
                    .orElse("🌐");

            select.setValue(currentFlag);
        } else {
            select.setValue("🌐");
        }

        select.addValueChangeListener(event -> {
            Locale selectedLocale = options.get(event.getValue());
            Locale currentLocale = UI.getCurrent().getLocale();

            if (selectedLocale != null && !selectedLocale.equals(currentLocale)) {
                UI.getCurrent().setLocale(selectedLocale);
                UI.getCurrent().getSession().setLocale(selectedLocale);
                UI.getCurrent().getPage().reload();
            }
        });
    }

    @Override
    public void localeChange(LocaleChangeEvent event) {}
}
