package pl.crewops;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class CrewOpsCore {
    public static void main(String[] args) {
        SpringApplication.run(CrewOpsCore.class, args);
        // TODO: find reason why core container shouting down
    }
}
