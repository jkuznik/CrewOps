package pl.crewops.component.grid;

import static pl.crewops.util.LocalDateTimeFormater.DATE_TIME_HUMAN_READABLE_FORMATTER;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.data.value.ValueChangeMode;
import java.time.ZoneId;
import java.util.List;
import java.util.Optional;
import org.springframework.security.core.context.SecurityContextHolder;
import pl.crewops.component.form.BreakdownForm;
import pl.crewops.component.notification.FailNotification;
import pl.crewops.component.notification.SuccessNotification;
import pl.crewops.exceptions.NotAuthenticatedException;
import pl.crewops.infrastructure.core.CoreAPI;
import pl.crewops.model.BreakdownFormModel;
import pl.crewops.model.dto.breakdown.BreakdownDTO;
import pl.crewops.model.dto.breakdown.UpdateBreakdownDTO;
import pl.crewops.security.custom.UserPrincipal;
import pl.crewops.util.AuthenticationResolver;
import pl.crewops.util.BrowserResolver;
import pl.crewops.view.HomeView;

public class BreakdownGrid extends VerticalLayout {
    private final CoreAPI coreAPI;
    private final AuthenticationResolver authenticationResolver;

    private final TextField typeFilter = new TextField();
    private final TextField descriptionFilter = new TextField();
    private final HorizontalLayout gridToolbar;

    private final Grid<BreakdownFormModel> grid = new Grid<>();
    private final BreakdownForm form;

    public BreakdownGrid(CoreAPI coreAPI, AuthenticationResolver authenticationResolver) {
        this.coreAPI = coreAPI;
        this.authenticationResolver = authenticationResolver;
        this.form = new BreakdownForm();
        gridToolbar = getToolbar();

        configureGrid();
        configureForm();

        localize();

        updateBreakdownGrid();
        closeEditor();

        setSizeFull();
        add(gridToolbar, getContent());
    }

    public void setTypeFilter(String value) {
        typeFilter.setValue(value);
        updateBreakdownGrid();
    }

    private HorizontalLayout getContent() {
        var content = new HorizontalLayout(grid, form);
        content.setSizeFull();
        content.setFlexGrow(2, grid);
        content.setFlexGrow(1, form);
        return content;
    }

    private HorizontalLayout getToolbar() {
        var toolbar = new HorizontalLayout();

        typeFilter.setClearButtonVisible(true);
        typeFilter.setValueChangeMode(ValueChangeMode.LAZY);
        typeFilter.addValueChangeListener(event -> updateBreakdownGrid());

        descriptionFilter.setClearButtonVisible(true);
        descriptionFilter.setValueChangeMode(ValueChangeMode.LAZY);
        descriptionFilter.addValueChangeListener(event -> updateBreakdownGrid());

        toolbar.add(typeFilter, descriptionFilter);

        return toolbar;
    }

    private void localize() {
        typeFilter.setPlaceholder(getTranslation("breakdownGrid.filter.registrationNumberPlaceholder"));
        descriptionFilter.setPlaceholder(getTranslation("breakdownGrid.filter.descriptionPlaceholder"));

        grid.getColumnByKey("registrationNumber").setHeader(getTranslation("breakdownGrid.column.registrationNumber"));
        grid.getColumnByKey("description").setHeader(getTranslation("breakdownGrid.column.description"));
        grid.getColumnByKey("critical").setHeader(getTranslation("breakdownGrid.column.critical"));
        grid.getColumnByKey("solved").setHeader(getTranslation("breakdownGrid.column.solved"));
        grid.getColumnByKey("reportedBy").setHeader(getTranslation("breakdownGrid.column.reportedBy"));
        grid.getColumnByKey("repairedBy").setHeader(getTranslation("breakdownGrid.column.repairedBy"));
        grid.getColumnByKey("solvedAt").setHeader(getTranslation("breakdownGrid.column.solvedAt"));
    }

    private void configureGrid() {
        grid.setSizeFull();

        grid.addColumn(model -> model.getMachine() != null ? model.getMachine().registerNumber() : "-")
                .setKey("registrationNumber");

        grid.addColumn(BreakdownFormModel::getDescription).setKey("description");

        grid.addColumn(new ComponentRenderer<>(model -> {
                    if (model.isCritical() && !model.isSolved()) {
                        Icon criticalIcon = VaadinIcon.CLOSE_CIRCLE.create();
                        criticalIcon.setColor("red");
                        return criticalIcon;
                    } else {
                        return new Span();
                    }
                }))
                .setHeader("Critical")
                .setKey("critical");

        grid.addColumn(new ComponentRenderer<>(model -> {
                    if (model.isSolved()) {
                        Icon solvedIcon = VaadinIcon.CHECK_CIRCLE.create();
                        solvedIcon.setColor("green");
                        return solvedIcon;
                    } else {
                        Icon pendingIcon = VaadinIcon.CLOCK.create();
                        pendingIcon.setColor("goldenrod");
                        return pendingIcon;
                    }
                }))
                .setHeader("Solved")
                .setKey("solved");

        grid.addColumn(model -> model.getReportedBy() != null
                        ? model.getReportedBy().firstName() + " "
                                + model.getReportedBy().lastName()
                        : "-")
                .setKey("reportedBy");

        grid.addColumn(model -> model.getRepairedBy() != null
                        ? model.getRepairedBy().firstName() + " "
                                + model.getRepairedBy().lastName()
                        : "-")
                .setKey("repairedBy");

        grid.addColumn(model -> model.getSolvedAt() != null
                        ? DATE_TIME_HUMAN_READABLE_FORMATTER
                                .withZone(ZoneId.systemDefault())
                                .format(model.getSolvedAt())
                        : "-")
                .setKey("solvedAt")
                .setHeader(getTranslation("breakdownGrid.column.solvedAt"));

        grid.getColumns().forEach(column -> {
            column.setFlexGrow(1);
            column.setAutoWidth(false);
        });

        grid.asSingleSelect().addValueChangeListener(event -> editBreakdown(event.getValue()));
    }

    private void configureForm() {
        if (BrowserResolver.isMobile()) {
            form.setWidthFull();
        } else {
            form.setWidth("25em");
        }

        form.addUpdateListener(this::updateBreakdown);
        form.addCloseListener(event -> closeEditor());
    }

    public void updateBreakdownGrid() {
        try {
            List<BreakdownFormModel> breakdowns = coreAPI.getAllBreakdowns().stream()
                    .map(BreakdownFormModel::toBreakdownFormModel)
                    .toList();

            String regFilter =
                    typeFilter.getValue() != null ? typeFilter.getValue().toLowerCase() : "";
            String descFilter = descriptionFilter.getValue() != null
                    ? descriptionFilter.getValue().toLowerCase()
                    : "";

            grid.setItems(breakdowns.stream()
                    .filter(breakdown -> breakdown.getMachine() != null
                            && breakdown
                                    .getMachine()
                                    .registerNumber()
                                    .toLowerCase()
                                    .contains(regFilter))
                    .filter(breakdown -> breakdown.getDescription() != null
                            && breakdown.getDescription().toLowerCase().contains(descFilter))
                    .toList());

        } catch (NotAuthenticatedException e) {
            notAuthenticatedAction(e);
        }
    }

    private void editBreakdown(BreakdownFormModel breakdownFormModel) {
        if (breakdownFormModel == null) {
            closeEditor();
        } else {
            form.setBreakdown(breakdownFormModel);
            if (authenticationResolver.principalHasOnlyEmployeePermission()) {
                form.setFormModeEmployeePermission();
            } else {
                form.setFormModeUpdate();
            }
            form.setVisible(true);

            if (BrowserResolver.isMobile()) {
                grid.setVisible(false);
                gridToolbar.setVisible(false);
                form.setWidthFull();
            }
        }
    }

    private void updateBreakdown(BreakdownForm.UpdateEvent event) {
        if (authenticationResolver.principalHasMechanicPermission()) {
            try {
                Optional<BreakdownDTO> breakdownDTO =
                        coreAPI.updateBreakdown(toUpdateBreakdownDTO(event.getBreakdown()));
                updateBreakdownGrid();
                closeEditor();
                if (breakdownDTO.isPresent()) {
                    new SuccessNotification(getTranslation("updateBreakdownNotification.messagePrefix"));
                }
            } catch (NotAuthenticatedException e) {
                notAuthenticatedAction(e);
            }
        }
    }

    public void closeEditor() {
        form.setBreakdown(null);
        form.setVisible(false);

        if (BrowserResolver.isMobile()) {
            grid.setVisible(true);
            gridToolbar.setVisible(true);
        }
    }

    private static void notAuthenticatedAction(NotAuthenticatedException e) {
        UI.getCurrent().navigate(HomeView.class);
        new FailNotification(e.getMessage());
    }

    private UpdateBreakdownDTO toUpdateBreakdownDTO(BreakdownFormModel breakdownFormModel) {
        var principal = (UserPrincipal)
                SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        return UpdateBreakdownDTO.builder()
                .breakdownId(breakdownFormModel.getId())
                .repairedByEmployeeId(principal.getEmployeeId())
                .solved(breakdownFormModel.isSolved())
                .build();
    }
}
