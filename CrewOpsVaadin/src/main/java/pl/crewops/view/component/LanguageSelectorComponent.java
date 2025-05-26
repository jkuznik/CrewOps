package pl.crewops.view.component;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.i18n.LocaleChangeEvent;
import com.vaadin.flow.i18n.LocaleChangeObserver;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;

@CssImport("./styles/component/language-selector.css")
public class LanguageSelectorComponent extends HorizontalLayout implements LocaleChangeObserver {

    private final Map<String, Locale> options = new LinkedHashMap<>();
    private final ComboBox<String> select = new ComboBox<>();

    public LanguageSelectorComponent() {
        fillOptions();

        configureComboBox();

        add(select);
    }

    private void configureComboBox() {
        select.setClassName("flag-combo");
        select.setItems(options.keySet());
        select.setLabel(null);

        if (UI.getCurrent() != null) {
            Locale currentLocale = UI.getCurrent().getLocale();

            // Znajdź odpowiadającą flagę na podstawie języka
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

    // TODO: automate this using CustomI18nProvider getProvidedLocales() method
    private void fillOptions() {
        options.put("🇺🇸", new Locale("en", "US")); // USA
        options.put("🇵🇱", new Locale("pl", "PL")); // Polska
        options.put("🇩🇪", new Locale("de", "DE")); // Niemcy
    }

    @Override
    public void localeChange(LocaleChangeEvent event) {}
}
