package pl.crewops;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import pl.crewops.security.SecurityProperties;

@SpringBootApplication
public class CrewOpsCore {
    public static void main(String[] args) {
        ConfigurableApplicationContext context = SpringApplication.run(CrewOpsCore.class, args);

        SecurityProperties securityProperties =
                (SecurityProperties) context.getBeanFactory().getBean("securityProperties");

        System.out.println(securityProperties.getJwtSecret());
        System.out.println(securityProperties.getJwtExpiration());
    }
}
