package pl.crewops.util;

import org.springframework.context.ApplicationContext;

public class SpringContextBridge {

    private static ApplicationContext context;

    public static void setApplicationContext(ApplicationContext applicationContext) {
        SpringContextBridge.context = applicationContext;
    }

    public static <T> T getBean(Class<T> requiredType) {
        return context.getBean(requiredType);
    }
}
