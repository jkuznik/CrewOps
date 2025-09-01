package pl.crewops.infrastructure.localization;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.server.UIInitEvent;
import com.vaadin.flow.server.UIInitListener;
import org.springframework.stereotype.Component;

@Component
public class DisableTranslateInitializer implements UIInitListener {

    @Override
    public void uiInit(UIInitEvent event) {
        UI ui = event.getUI();
        ui.getPage().executeJs("document.querySelector('html').setAttribute('translate', 'no');");
    }

    public static void disableTranslate(UI ui) {
        ui.getPage()
                .executeJs("document.documentElement.setAttribute('translate', 'no');"
                        + "document.body.setAttribute('translate', 'no');"
                        + "var meta = document.createElement('meta');"
                        + "meta.name = 'google';"
                        + "meta.content = 'notranslate';"
                        + "document.head.appendChild(meta);");
    }
}
