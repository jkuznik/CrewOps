package pl.crewops.infrastructure.localization;

import com.vaadin.flow.i18n.I18NProvider;
import java.text.MessageFormat;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;
import org.springframework.stereotype.Component;

@Component
public class CustomI18NProvider implements I18NProvider {

    private static final String BUNDLE_PREFIX = "i18n/messages";
    private final List<Locale> supportedLocales = List.of(new Locale("en", "US"), new Locale("pl", "PL"));

    @Override
    public List<Locale> getProvidedLocales() {
        return supportedLocales;
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

    private ResourceBundle getBundle(Locale locale) {
        return ResourceBundle.getBundle(BUNDLE_PREFIX, locale);
    }
}
