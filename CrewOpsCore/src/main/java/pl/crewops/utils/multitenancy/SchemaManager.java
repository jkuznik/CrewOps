package pl.crewops.utils.multitenancy;

import java.sql.Connection;
import java.sql.Statement;
import javax.sql.DataSource;
import org.springframework.stereotype.Component;
import pl.crewops.exception.multitenancy.CreateSchemaException;

@Component
public class SchemaManager {

    private final DataSource dataSource;

    public SchemaManager(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public void createSchemaIfNotExists(String schema) throws CreateSchemaException {
        try (Connection connection = dataSource.getConnection();
                Statement statement = connection.createStatement()) {
            statement.execute("CREATE SCHEMA IF NOT EXISTS " + schema);
        } catch (Exception e) {
            throw new CreateSchemaException("Failed to create tenant schema: " + schema, e);
        }
    }

    public void dropSchema(String schemaName) {
        try (Connection connection = dataSource.getConnection();
                Statement statement = connection.createStatement()) {

            statement.execute("DROP SCHEMA IF EXISTS " + schemaName + " CASCADE");

        } catch (Exception e) {
            throw new CreateSchemaException("Failed to drop tenant schema: " + schemaName, e);
        }
    }
}
