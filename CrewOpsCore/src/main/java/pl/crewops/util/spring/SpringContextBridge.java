package pl.crewops.util.spring;

import org.springframework.context.ApplicationContext;

public class SpringContextBridge {

    private static ApplicationContext context;

    public static void setApplicationContext(ApplicationContext context) {
        SpringContextBridge.context = context;
    }

    public static <T> T getBean(Class<T> requiredType) {
        return context.getBean(requiredType);
    }
}
