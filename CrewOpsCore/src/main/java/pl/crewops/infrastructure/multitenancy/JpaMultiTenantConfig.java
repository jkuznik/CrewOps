package pl.crewops.infrastructure.multitenancy;

import java.util.HashMap;
import java.util.Map;
import javax.sql.DataSource;
import org.hibernate.cfg.Environment;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;

@Configuration
public class JpaMultiTenantConfig {

    private final DataSource dataSource;
    private final SchemaPerTenantConnectionProvider connectionProvider;
    private final TenantIdentifierResolver tenantIdentifierResolver;

    public JpaMultiTenantConfig(
            DataSource dataSource,
            SchemaPerTenantConnectionProvider connectionProvider,
            TenantIdentifierResolver tenantIdentifierResolver) {
        this.dataSource = dataSource;
        this.connectionProvider = connectionProvider;
        this.tenantIdentifierResolver = tenantIdentifierResolver;
    }

    @Bean
    public LocalContainerEntityManagerFactoryBean entityManagerFactory(EntityManagerFactoryBuilder builder) {
        Map<String, Object> properties = new HashMap<>();
        properties.put("hibernate.multiTenancy", "SCHEMA");
        properties.put(Environment.MULTI_TENANT_CONNECTION_PROVIDER, connectionProvider);
        properties.put(Environment.MULTI_TENANT_IDENTIFIER_RESOLVER, tenantIdentifierResolver);

        return builder.dataSource(dataSource)
                .packages("pl.crewops.model")
                .properties(properties)
                .build();
    }
}
