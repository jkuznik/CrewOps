package pl.crewops;

import com.vaadin.flow.component.dependency.NpmPackage;
import com.vaadin.flow.component.page.AppShellConfigurator;
import com.vaadin.flow.component.page.Push;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import pl.crewops.util.SpringContextBridge;

@Push
@SpringBootApplication
@NpmPackage(value = "line-awesome", version = "1.3.0")
@NpmPackage(value = "@polymer/iron-iconset-svg", version = "3.0.1")
public class CrewOpsVaadin implements AppShellConfigurator {
    public static void main(String[] args) {
        ConfigurableApplicationContext context = SpringApplication.run(CrewOpsVaadin.class, args);
        SpringContextBridge.setApplicationContext(context);
    }
}
