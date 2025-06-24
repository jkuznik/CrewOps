package pl.crewops;

import java.sql.Connection;
import java.sql.Statement;
import javax.sql.DataSource;
import org.springframework.stereotype.Component;

@Component
public class TenantSchemaInitializer {

    private final DataSource dataSource;

    public TenantSchemaInitializer(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public void createSchemaIfNotExists(String schema) {
        try (Connection connection = dataSource.getConnection();
                Statement statement = connection.createStatement()) {
            statement.execute("CREATE SCHEMA IF NOT EXISTS " + schema);
        } catch (Exception e) {
            throw new RuntimeException("Failed to create tenant schema: " + schema, e);
        }
    }
}
