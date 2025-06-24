package pl.crewops.utils.liquibase;

import java.sql.Connection;
import javax.sql.DataSource;
import liquibase.Contexts;
import liquibase.LabelExpression;
import liquibase.Liquibase;
import liquibase.database.Database;
import liquibase.database.DatabaseFactory;
import liquibase.database.jvm.JdbcConnection;
import liquibase.resource.ClassLoaderResourceAccessor;
import org.springframework.stereotype.Component;

@Component
public class LiquibaseSchemaMigrator {

    private final DataSource dataSource;

    public LiquibaseSchemaMigrator(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public void runMigrations(String schemaName) {
        try (Connection connection = dataSource.getConnection()) {
            connection.createStatement().execute("SET search_path TO " + schemaName);

            Database database =
                    DatabaseFactory.getInstance().findCorrectDatabaseImplementation(new JdbcConnection(connection));

            database.setDefaultSchemaName(schemaName);

            Liquibase liquibase =
                    new Liquibase("db/changelog/db.changelog-tenant.yaml", new ClassLoaderResourceAccessor(), database);

            liquibase.update(new Contexts(), new LabelExpression());
        } catch (Exception e) {
            throw new RuntimeException("Liquibase migration failed for schema: " + schemaName, e);
        }
    }
}
