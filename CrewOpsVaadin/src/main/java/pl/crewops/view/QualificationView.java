package pl.crewops.view;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.spring.annotation.SpringComponent;
import jakarta.annotation.security.PermitAll;
import java.util.List;
import org.springframework.context.annotation.Scope;
import pl.crewops.dto.qualification.QualificationDTO;
import pl.crewops.dto.qualification.QualificationFormModel;
import pl.crewops.infrastructure.core.CoreAPI;
import pl.crewops.view.component.QualificationForm;

@SpringComponent
@Scope("prototype")
@PermitAll
@Route(value = "qualifications", layout = MainLayout.class)
@PageTitle("Qualification management")
public class QualificationView extends VerticalLayout {
    Grid<QualificationDTO> grid = new Grid<>(QualificationDTO.class);
    TextField filterText = new TextField();
    QualificationForm form;
    CoreAPI coreAPI;

    public QualificationView(CoreAPI coreAPI) {
        addClassName("qualification-view");

        this.coreAPI = coreAPI;

        setSizeFull();
        configureGrid();
        configureForm();

        add(getToolbar(), getContent());
        updateList();
        closeEditor();
    }

    private HorizontalLayout getContent() {
        HorizontalLayout content = new HorizontalLayout(grid, form);
        content.setFlexGrow(2, grid);
        content.setFlexGrow(1, form);
        content.addClassNames("content");
        content.setSizeFull();
        return content;
    }

    private void configureForm() {
        form = new QualificationForm(coreAPI);
        form.setWidth("25em");

        form.addSaveListener(this::saveContact);
        form.addDeleteListener(this::deleteContact);
        form.addCloseListener(e -> closeEditor());
    }

    private void saveContact(QualificationForm.SaveEvent event) {
        coreAPI.createQualification(QualificationFormModel.toCreateQualificationDTO(event.getQualification()));
        updateList();
        closeEditor();
    }

    private void deleteContact(QualificationForm.DeleteEvent event) {
        //            service.deleteContact(event.getContact());
        updateList();
        closeEditor();
    }

    // TODO: add column that contains employees amount with each qualification
    private void configureGrid() {
        grid.addClassNames("qualification-grid");
        grid.setSizeFull();
        grid.setColumns("description");
        grid.getColumns().forEach(col -> col.setAutoWidth(true));

        grid.asSingleSelect().addValueChangeListener(event -> editQualification(event.getValue()));
    }

    private Component getToolbar() {
        filterText.setPlaceholder("Filter by name...");
        filterText.setClearButtonVisible(true);
        filterText.setValueChangeMode(ValueChangeMode.LAZY);
        filterText.addValueChangeListener(e -> updateList());

        Button addContactButton = new Button("Add qualification");
        addContactButton.addClickListener(click -> addEmployee());

        var toolbar = new HorizontalLayout(filterText, addContactButton);
        toolbar.addClassName("toolbar");
        return toolbar;
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

    private void closeEditor() {
        form.setQualification(null);
        form.setVisible(false);
        removeClassName("editing");
    }

    // TODO: implement logic
    private void addEmployee() {
        grid.asSingleSelect().clear();
        form.setVisible(true);
    }

    private void updateList() {
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
    }
}
