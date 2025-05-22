package pl.crewops.infrastructure.localization;

import com.vaadin.flow.i18n.I18NProvider;
import java.text.MessageFormat;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;
import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Component;

@Component
@Getter
@Setter
public class CustomI18NProvider implements I18NProvider {

    private static final String BUNDLE_PREFIX = "i18n/messages";
    //    private Locale currentLocale = new Locale("de", "DE");
    private Locale currentLocale;

    @Override
    public List<Locale> getProvidedLocales() {
        return List.of(new Locale("en", "US"), new Locale("pl", "PL"), new Locale("de", "DE"));
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
