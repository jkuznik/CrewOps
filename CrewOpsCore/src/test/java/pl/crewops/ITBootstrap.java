package pl.crewops;

import java.sql.Connection;
import javax.sql.DataSource;
import liquibase.Contexts;
import liquibase.LabelExpression;
import liquibase.Liquibase;
import liquibase.database.Database;
import liquibase.database.DatabaseFactory;
import liquibase.database.jvm.JdbcConnection;
import liquibase.resource.ClassLoaderResourceAccessor;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import pl.crewops.exception.multitenancy.CreateSchemaException;
import pl.crewops.utils.multitenancy.LiquibaseSchemaMigrator;
import pl.crewops.utils.multitenancy.SchemaManager;

@Component
@Profile(value = "test")
public class ITBootstrap {

    private final SchemaManager schemaManager;
    private final LiquibaseSchemaMigrator liquibaseSchemaMigrator;
    private final DataSource dataSource;

    public ITBootstrap(
            SchemaManager schemaManager, LiquibaseSchemaMigrator liquibaseSchemaMigrator, DataSource dataSource) {
        this.schemaManager = schemaManager;
        this.liquibaseSchemaMigrator = liquibaseSchemaMigrator;
        this.dataSource = dataSource;

        String schemaName = IntegrationTest.TEST_SCHEMA_NAME;

        schemaManager.createSchemaIfNotExists(schemaName);
        liquibaseSchemaMigrator.runMigrations(schemaName);

        try (Connection connection = dataSource.getConnection()) {
            connection.createStatement().execute("SET search_path TO " + schemaName);

            Database database =
                    DatabaseFactory.getInstance().findCorrectDatabaseImplementation(new JdbcConnection(connection));

            database.setDefaultSchemaName(schemaName);

            Liquibase liquibase =
                    new Liquibase("db/changelog/db.changelog-test.yaml", new ClassLoaderResourceAccessor(), database);

            liquibase.update(new Contexts(), new LabelExpression());
        } catch (Exception e) {
            throw new CreateSchemaException("Liquibase migration failed for schema: " + schemaName, e);
        }
    }
}
