package pl.crewops.ui.component.panel;

import com.vaadin.flow.component.ComponentEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.shared.Registration;
import pl.crewops.model.dto.employee.EmployeeDTO;
import pl.crewops.model.dto.jobPosition.JobPositionDTO;
import pl.crewops.ui.component.custom.ComboBoxCustom;
import pl.crewops.ui.component.custom.PanelCustom;

public class ShiftPanel extends PanelCustom {

    // todo i18n
    public TextField name = new TextField("Name");

    public ComboBoxCustom<JobPositionDTO> jobPositions = new ComboBoxCustom<>();

    public ComboBoxCustom<EmployeeDTO> relatedEmployees = new ComboBoxCustom<>();

    public Checkbox critical = new Checkbox("Critical");

    public Button create = new Button("Create");
    public Button close = new Button("Close");

    public ShiftPanel() {
        setHeight("550px");
        setSummary(VaadinIcon.MEDAL, "Shift creator");

        close.addClickListener(event -> {
            this.setVisible(false);
            fireEvent(new CloseEvent(this));
        });

        var mainContainer = new VerticalLayout();
        mainContainer.setSpacing(true);
        mainContainer.setPadding(true);

        var buttonLayout = new HorizontalLayout();
        buttonLayout.setSpacing(true);
        buttonLayout.setPadding(true);
        buttonLayout.add(create, close);

        mainContainer.add(name, jobPositions, relatedEmployees, critical, buttonLayout);

        addContent(mainContainer);
    }

    public abstract static class ShiftPanelEvent extends ComponentEvent<PanelCustom> {
        public ShiftPanelEvent(PanelCustom source) {
            super(source, false);
        }
    }

    public static class CloseEvent extends ShiftPanelEvent {
        public CloseEvent(PanelCustom source) {
            super(source);
        }
    }

    public Registration addCloseListener(ComponentEventListener<CloseEvent> listener) {
        return addListener(CloseEvent.class, listener);
    }
}
