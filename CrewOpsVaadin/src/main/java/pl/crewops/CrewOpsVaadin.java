package pl.crewops;

import com.vaadin.flow.component.dependency.NpmPackage;
import com.vaadin.flow.component.page.AppShellConfigurator;
import com.vaadin.flow.component.page.Push;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.ConfigurableApplicationContext;
import pl.crewops.util.SpringContextBridge;

@Push
@EnableCaching
@SpringBootApplication
@NpmPackage(value = "line-awesome", version = "1.3.0")
public class CrewOpsVaadin implements AppShellConfigurator {
    public static void main(String[] args) {
        ConfigurableApplicationContext context = SpringApplication.run(CrewOpsVaadin.class, args);
        SpringContextBridge.setApplicationContext(context);

        // TODO: find why getCompanyById is calling twice on each UI navigation action

        // TODO: modify localdate and localdatetime display way in grids
    }
}
