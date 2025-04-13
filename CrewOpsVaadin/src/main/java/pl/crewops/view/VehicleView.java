package pl.crewops.view;

import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.spring.annotation.SpringComponent;
import java.awt.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Scope;
import pl.crewops.dto.vehicle.VehicleDTO;

@SpringComponent
@Route(value = "vehicles", layout = MainLayout.class)
@Scope("prototype")
@Slf4j
@PageTitle("Vehicle view")
public class VehicleView extends VerticalLayout {

    Grid<VehicleDTO> grid = new Grid<>(VehicleDTO.class);
    TextField filter = new TextField("Filter");
}
