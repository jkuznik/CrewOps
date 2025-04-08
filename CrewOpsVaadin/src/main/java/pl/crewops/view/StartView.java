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
import org.springframework.context.annotation.Scope;
import pl.crewops.dto.employee.EmployeeDTO;
import pl.crewops.infrastructure.core.CoreClient;

@SpringComponent
@Scope("prototype")
@PermitAll
@Route(value = "", layout = MainLayout.class)
@PageTitle("Home page")
public class StartView extends VerticalLayout {
    Grid<EmployeeDTO> grid = new Grid<>(EmployeeDTO.class);
    TextField filterText = new TextField();
    EmployeeForm form;
    CoreClient coreClient;
    //    CrmService service;

    public StartView(CoreClient coreClient) {
        this.coreClient = coreClient;
        addClassName("list-view");
        setSizeFull();
        configureGrid();
        configureForm();

        add(getToolbar(), getContent());
        updateList();
        //        closeEditor();
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
        EmployeeDTO first = coreClient.getEmployees().getFirst();
        form = new EmployeeForm(first);
        form.setWidth("25em");
        //        form.addSaveListener(this::saveContact); // <1>
        //        form.addDeleteListener(this::deleteContact); // <2>
        //        form.addCloseListener(e -> closeEditor()); // <3>
    }

    //    private void saveContact(ContactForm.SaveEvent event) {
    //        service.saveContact(event.getContact());
    //        updateList();
    //        closeEditor();
    //    }

    //    private void deleteContact(ContactForm.DeleteEvent event) {
    //        service.deleteContact(event.getContact());
    //        updateList();
    //        closeEditor();
    //    }

    private void configureGrid() {
        grid.addClassNames("employee-grid");
        grid.setSizeFull();
        grid.setColumns("firstName", "lastName", "birthDate", "phoneNumber", "department");
        grid.addColumn(EmployeeDTO::qualifications).setHeader("Qualifications");
        grid.addColumn(EmployeeDTO::vehicles).setHeader("Vehicles");
        grid.getColumns().forEach(col -> col.setAutoWidth(true));

        //        grid.asSingleSelect().addValueChangeListener(event ->
        //                editContact(event.getValue()));
    }

    private Component getToolbar() {
        filterText.setPlaceholder("Filter by name...");
        filterText.setClearButtonVisible(true);
        filterText.setValueChangeMode(ValueChangeMode.LAZY);
        //        filterText.addValueChangeListener(e -> updateList());

        Button addContactButton = new Button("Add contact");
        //        addContactButton.addClickListener(click -> addContact());

        var toolbar = new HorizontalLayout(filterText, addContactButton);
        toolbar.addClassName("toolbar");
        return toolbar;
    }

    //    public void editContact(Contact contact) {
    //        if (contact == null) {
    //            closeEditor();
    //        } else {
    //            form.setContact(contact);
    //            form.setVisible(true);
    //            addClassName("editing");
    //        }
    //    }
    //
    //    private void closeEditor() {
    //        form.setContact(null);
    //        form.setVisible(false);
    //        removeClassName("editing");
    //    }

    //    private void addContact() {
    //        grid.asSingleSelect().clear();
    //        editContact(new Contact());
    //    }
    //
    private void updateList() {
        grid.setItems(coreClient.getEmployees());
    }
}
