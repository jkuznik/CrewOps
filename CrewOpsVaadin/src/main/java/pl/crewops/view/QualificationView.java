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
import pl.crewops.dto.qualification.QualificationDTO;
import pl.crewops.exceptions.NotAuthenticatedException;
import pl.crewops.infrastructure.core.CoreAPI;
import pl.crewops.security.jwt.JwtInfoService;
import pl.crewops.view.component.mainLayout.MainLayout;
import pl.crewops.view.form.QualificationForm;
import pl.crewops.view.form.model.QualificationFormModel;

@Route(value = "qualifications")
@PageTitle("Qualification management")
public class QualificationView extends MainLayout implements BeforeEnterObserver {
    Grid<QualificationDTO> grid = new Grid<>(QualificationDTO.class);
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

        updateList();
        closeEditor();
    }

    private Component getToolbar() {
        filterText.setLabel("Filter by name...");
        filterText.setClearButtonVisible(true);
        filterText.setValueChangeMode(ValueChangeMode.LAZY);
        filterText.addValueChangeListener(e -> updateList());

        Button addQualificationButton = new Button("Add qualification");
        addQualificationButton.addClickListener(click -> addEmployee());

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

        form.addSaveListener(this::saveContact);
        form.addDeleteListener(this::deleteContact);
        form.addCloseListener(e -> closeEditor());
    }

    private void updateList() {
        try {
            List<QualificationDTO> employees = coreAPI.getAllQualifications();

            if (filterText.getValue() == null) {
                grid.setItems(employees);
            } else {
                grid.setItems(employees.stream()
                        .filter(qualificationDTO -> qualificationDTO
                                .description()
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

    private void saveContact(QualificationForm.SaveEvent event) {
        try {
            // TODO: add 'unique description' validation
            coreAPI.createQualification(QualificationFormModel.toCreateQualificationDTO(event.getQualification()));
            updateList();
            closeEditor();
        } catch (NotAuthenticatedException e) {
            // TODO: implement logic
        }
    }

    private void deleteContact(QualificationForm.DeleteEvent event) {
        try {
            coreAPI.deleteQualification(event.getQualification().getId());
            updateList();
            closeEditor();
        } catch (NotAuthenticatedException e) {
            // TODO: implement logic
        }
    }

    public void editQualification(QualificationDTO qualificationDTO) {
        if (qualificationDTO == null) {
            closeEditor();
        } else {
            form.setQualification(qualificationDTO);
            form.setVisible(true);
            addClassName("editing");
        }
    }

    // TODO: add column that contains employees amount with each qualification

    private void addEmployee() {
        grid.asSingleSelect().clear();
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
