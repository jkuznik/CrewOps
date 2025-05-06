package pl.crewops.view;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import java.util.List;
import pl.crewops.exceptions.NotAuthenticatedException;
import pl.crewops.infrastructure.core.CoreAPI;
import pl.crewops.security.jwt.JwtInfoService;
import pl.crewops.view.component.mainLayout.MainLayout;
import pl.crewops.view.form.QualificationForm;
import pl.crewops.view.form.model.QualificationFormModel;

@Route(value = "qualifications")
@PageTitle("Qualification management")
public class QualificationView extends MainLayout implements BeforeEnterObserver {
    Grid<QualificationFormModel> grid = new Grid<>(QualificationFormModel.class);
    TextField filterText = new TextField();
    QualificationForm form;

    public QualificationView(CoreAPI coreAPI, JwtInfoService jwtInfoService) {
        super(coreAPI, jwtInfoService);
        addClassName("qualification-view");
        VerticalLayout currentContent = new VerticalLayout();
        currentContent.setId("view-content");

        mainContent.removeAll();
        mainContent.add(currentContent, mainFooter);
        mainContent.setFlexGrow(1, currentContent);

        currentContent.setSizeFull();
        currentContent.setPadding(true);
        currentContent.setSpacing(true);
        currentContent.getStyle().set("overflow", "auto");

        configureGrid();
        configureForm();

        currentContent.add(getToolbar(), getCurrentContent());

        updateGrid();
        closeEditor();
    }

    private Component getToolbar() {
        filterText.setLabel("Filter by name...");
        filterText.setClearButtonVisible(true);
        filterText.setValueChangeMode(ValueChangeMode.LAZY);
        filterText.addValueChangeListener(e -> updateGrid());

        Button addQualificationButton = new Button("Add qualification");
        addQualificationButton.addClickListener(click -> addQualification());

        var toolbar = new HorizontalLayout(filterText, addQualificationButton);
        toolbar.addClassName("qualification-toolbar");
        toolbar.setAlignItems(FlexComponent.Alignment.END);
        return toolbar;
    }

    private HorizontalLayout getCurrentContent() {
        HorizontalLayout content = new HorizontalLayout(grid, form);
        content.addClassNames("qualification-view-content");
        content.setFlexGrow(2, grid);
        content.setFlexGrow(1, form);
        content.setSizeFull();
        return content;
    }

    private void configureGrid() {
        grid.addClassNames("qualification-grid");
        grid.setSizeFull();
        grid.setColumns("description");
        grid.getColumns().forEach(col -> col.setAutoWidth(true));

        grid.asSingleSelect().addValueChangeListener(event -> editQualification(event.getValue()));
    }

    private void configureForm() {
        form = new QualificationForm();
        form.setWidth("25em");

        form.addSaveListener(this::saveQualification);
        form.addUpdateListener(this::updateQualification);
        form.addDeleteListener(this::deleteQualification);
        form.addCloseListener(e -> closeEditor());
    }

    private void updateGrid() {
        try {
            List<QualificationFormModel> employees = coreAPI.getAllQualifications().stream()
                    .map(QualificationFormModel::toQualificationFormModel)
                    .toList();

            if (filterText.getValue() == null) {
                grid.setItems(employees);
            } else {
                grid.setItems(employees.stream()
                        .filter(qualificationDTO -> qualificationDTO
                                .getDescription()
                                .toLowerCase()
                                .contains(filterText.getValue().toLowerCase()))
                        .toList());
            }
        } catch (NotAuthenticatedException e) {
            // TODO: implement logic
        }
    }

    private void closeEditor() {
        form.setQualification(null);
        form.setVisible(false);
        removeClassName("editing");
    }

    private void saveQualification(QualificationForm.SaveEvent event) {
        try {
            // TODO: add 'unique description' validation
            coreAPI.createQualification(QualificationFormModel.toCreateQualificationDTO(event.getQualification()));
            updateGrid();
            closeEditor();
        } catch (NotAuthenticatedException e) {
            // TODO: implement logic
        }
    }

    private void updateQualification(QualificationForm.UpdateEvent event) {
        try {
            coreAPI.updateQualification(QualificationFormModel.toUpdateQualificationDTO(event.getQualification()));
            updateGrid();
            closeEditor();
        } catch (NotAuthenticatedException e) {
            // TODO: implement logic
        }
    }

    private void deleteQualification(QualificationForm.DeleteEvent event) {
        try {
            coreAPI.deleteQualification(event.getQualification().getId());
            updateGrid();
            closeEditor();
        } catch (NotAuthenticatedException e) {
            // TODO: implement logic
        }
    }

    public void editQualification(QualificationFormModel qualificationFormModel) {
        if (qualificationFormModel == null) {
            closeEditor();
        } else {
            form.setQualification(qualificationFormModel);
            form.setFormModeUpdate();
            form.setVisible(true);
            addClassName("editing");
        }
    }

    // TODO: add column that contains employees amount with each qualification

    private void addQualification() {
        grid.asSingleSelect().clear();
        form.setFormModeSave();
        form.setVisible(true);
    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        if (!jwtInfoService.validToken()) {
            // TODO: try to show notification in this case
            // TODO: add some listener of any action (add, edit, delete employee, etc.), currently navigation work only
            // in case entering into secured view
            event.forwardTo(HomeView.class);
            UI.getCurrent().getPage().setLocation("/");
        }
    }
}
