// package pl.crewops.ui.component.custom;
//
// import com.vaadin.flow.component.button.Button;
// import com.vaadin.flow.component.checkbox.Checkbox;
// import com.vaadin.flow.component.orderedlayout.VerticalLayout;
// import com.vaadin.flow.component.textfield.TextField;
// import java.util.Set;
// import pl.crewops.infrastructure.core.CoreAPI;
// import pl.crewops.model.dto.employee.EmployeeDTO;
// import pl.crewops.model.dto.jobPosition.JobPositionDTO;
// import pl.crewops.model.dto.shift.CreateShiftDTO;
// import pl.crewops.model.dto.shift.ShiftConfig;
// import pl.crewops.util.SpringContextBridge;
//
// public class PoCShiftCreator extends VerticalLayout {
//
//    public TextField name = new TextField("Name");
//
//    public ComboBoxCustom<JobPositionDTO> jobPositions = new ComboBoxCustom<>();
//
//    public ComboBoxCustom<EmployeeDTO> relatedEmployees = new ComboBoxCustom<>();
//
//    public Checkbox critical = new Checkbox("Critical");
//
//    public Button create = new Button("Create");
//
//    public PoCShiftCreator() {
//        var coreAPI = SpringContextBridge.getBean(CoreAPI.class);
//
//        setSizeFull();
//
//        jobPositions.setItemLabelGenerator(JobPositionDTO::name);
//        relatedEmployees.setItemLabelGenerator(EmployeeDTO::firstName);
//
//        add(name, jobPositions, relatedEmployees, critical, create);
//
//        try {
//            jobPositions.setItems(coreAPI.getAllJobPositions());
//            relatedEmployees.setItems(coreAPI.getAllEmployees());
//
//            create.addClickListener(e -> {
//                ShiftConfig shiftConfig = ShiftConfig.builder()
//                        .jopPosition(jobPositions.getValue())
//                        .critical(critical.getValue())
//                        .relatedEmployee(relatedEmployees.getValue())
//                        .build();
//
//                CreateShiftDTO createShiftDTO = CreateShiftDTO.builder()
//                        .name(name.getValue())
//                        .jobPositions(Set.of(jobPositions.getValue()))
//                        .configs(Set.of(shiftConfig))
//                        .build();
//
//                try {
//                    coreAPI.createShift(createShiftDTO);
//
//                } catch (Exception ex) {
//                    System.out.println(ex.getMessage());
//                }
//            });
//        } catch (Exception e) {
//            System.out.println(e.getMessage());
//        }
//    }
// }
