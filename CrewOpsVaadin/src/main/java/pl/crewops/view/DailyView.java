package pl.crewops.view;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import java.time.LocalDate;
import java.util.List;
import pl.crewops.component.custom.DailyTimeline;
import pl.crewops.component.dialog.dateSelectorDialog.DateSelectorDialog;
import pl.crewops.infrastructure.core.CoreAPI;
import pl.crewops.security.jwt.JwtServiceVaadin;
import pl.crewops.util.AuthenticationResolver;
import pl.crewops.view.layout.MainLayout;

@Route("daily")
@PageTitle("Daily Entry")
public class DailyView extends MainLayout implements BeforeEnterObserver {

    private final DailyTimeline timeline = new DailyTimeline();
    private final Span information = new Span();

    private boolean isDailyEntryExist;

    public DailyView(CoreAPI coreAPI, JwtServiceVaadin jwtService, AuthenticationResolver authenticationResolver) {
        super(coreAPI, jwtService, authenticationResolver);
    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        if (authenticationResolver.principalIsAuthenticated()) {
            buildContent();
        } else {
            event.forwardTo(HomeView.class);
            UI.getCurrent().getPage().setLocation("/");
        }
    }

    private void buildContent() {
        mainContent.removeAll();
        updateTimeline(LocalDate.now());

        mainContent.add(getToolbar(), information, timeline);
    }

    private HorizontalLayout getToolbar() {
        var toolbar = new HorizontalLayout();

        // todo: i18n
        Button currentDay = new Button("Dzisiaj");
        Button history = new Button("Historia");
        currentDay.addClickListener(event -> {
            updateTimeline(LocalDate.now());

            timeline.setVisible(true);
        });
        history.addClickListener(event -> {
            var dateSelectorDialog = new DateSelectorDialog();
            dateSelectorDialog.addSelectDateListener(selectedDateEvent -> {
                updateTimeline(selectedDateEvent.getSelectedDate());
            });
        });

        toolbar.add(currentDay, history);

        return toolbar;
    }

    private void updateTimeline(LocalDate selectedDate) {
        //        isDailyEntryExist = coreAPI.getDailyEntryDateAndEmployeeId(LocalDate.now(),
        // authenticationResolver.getPrincipal().getEmployeeId());

        if (!isDailyEntryExist) {
            if (selectedDate.equals(LocalDate.now())) {
                information.setText(
                        "W dzienniku pracy nie zarejstrowano wpisu dla dzisiejszego dnia. Zaktualizuj informacje aby utworzyć trwały wpis do dziennika ");
            } else {
                information.setText("W dzienniku pracy nie zarejestrowano wpisu dla " + selectedDate.toString()
                        + ". Zaktualizuj informacje aby utworzyc trawły wpis do dziennika");
            }
            information.setVisible(true);
        }
        timeline.updateItems(List.of());
    }
}
