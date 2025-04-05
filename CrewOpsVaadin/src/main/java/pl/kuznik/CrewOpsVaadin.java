package pl.kuznik;

import com.vaadin.flow.component.dependency.NpmPackage;
import com.vaadin.flow.component.page.AppShellConfigurator;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@NpmPackage(value = "line-awesome", version = "1.3.0")
public class CrewOpsVaadin implements AppShellConfigurator {
    public static void main(String[] args) {
        SpringApplication.run(CrewOpsVaadin.class, args);
    }
}
