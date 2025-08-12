package pl.crewops;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import pl.crewops.util.spring.SpringContextBridge;

@SpringBootApplication
public class CrewOpsCore {
    public static void main(String[] args) {
        ConfigurableApplicationContext context = SpringApplication.run(CrewOpsCore.class, args);
        SpringContextBridge.setApplicationContext(context);
    }
}
