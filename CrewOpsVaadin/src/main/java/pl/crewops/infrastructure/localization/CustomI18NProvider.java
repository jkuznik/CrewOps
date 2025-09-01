package pl.crewops.infrastructure.localization;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.i18n.I18NProvider;
import com.vaadin.flow.server.UIInitEvent;
import com.vaadin.flow.server.UIInitListener;
import java.text.MessageFormat;
import java.util.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Component;

@Component
@Getter
@Setter
public class CustomI18NProvider implements I18NProvider, UIInitListener {

    private static final String BUNDLE_PREFIX = "i18n/messages";

    private static final Map<String, Locale> FLAG_TO_LOCALE = Map.of(
            "🇺🇸", new Locale("en", "US"),
            "🇵🇱", new Locale("pl", "PL"),
            "🇩🇪", new Locale("de", "DE"));

    private final List<Locale> LOCALES = List.copyOf(FLAG_TO_LOCALE.values());

    public static Map<String, Locale> getFlagToLocale() {
        return FLAG_TO_LOCALE;
    }

    @Override
    public List<Locale> getProvidedLocales() {
        return LOCALES;
    }

    @Override
    public String getTranslation(String key, Locale locale, Object... params) {
        if (key == null) return "";

        ResourceBundle bundle = getBundle(locale);

        if (!bundle.containsKey(key)) {
            return "!!" + key + "!!";
        }

        String value = bundle.getString(key);
        return params.length > 0 ? MessageFormat.format(value, params) : value;
    }

    @Override
    public void uiInit(UIInitEvent event) {
        UI ui = event.getUI();
        ui.getPage().executeJs("document.querySelector('html').setAttribute('translate', 'no');");
    }

    private ResourceBundle getBundle(Locale locale) {
        return ResourceBundle.getBundle(BUNDLE_PREFIX, locale);
    }
}
