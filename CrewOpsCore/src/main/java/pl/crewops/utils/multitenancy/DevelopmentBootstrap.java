package pl.crewops.utils.multitenancy;

import java.sql.Connection;
import javax.sql.DataSource;
import liquibase.Contexts;
import liquibase.LabelExpression;
import liquibase.Liquibase;
import liquibase.database.Database;
import liquibase.database.DatabaseFactory;
import liquibase.database.jvm.JdbcConnection;
import liquibase.resource.ClassLoaderResourceAccessor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import pl.crewops.exception.multitenancy.CreateSchemaException;

@Slf4j
@Component
@Profile(value = "dev")
public class DevelopmentBootstrap {

    private final TenantSchemaInitializer tenantSchemaInitializer;
    private final LiquibaseSchemaMigrator liquibaseSchemaMigrator;
    private final DataSource dataSource;

    public DevelopmentBootstrap(
            TenantSchemaInitializer tenantSchemaInitializer,
            LiquibaseSchemaMigrator liquibaseSchemaMigrator,
            DataSource dataSource) {
        this.tenantSchemaInitializer = tenantSchemaInitializer;
        this.liquibaseSchemaMigrator = liquibaseSchemaMigrator;
        this.dataSource = dataSource;

        String schemaName = "testtenant_2f3b1d5c9e8f";

        tenantSchemaInitializer.createSchemaIfNotExists(schemaName);
        liquibaseSchemaMigrator.runMigrations(schemaName);

        try (Connection connection = dataSource.getConnection()) {
            connection.createStatement().execute("SET search_path TO " + schemaName);

            Database database =
                    DatabaseFactory.getInstance().findCorrectDatabaseImplementation(new JdbcConnection(connection));

            database.setDefaultSchemaName(schemaName);

            Liquibase liquibase = new Liquibase(
                    "db/changelog/insert/001-insert-development-initial-values.yaml",
                    new ClassLoaderResourceAccessor(),
                    database);

            liquibase.update(new Contexts(), new LabelExpression());
        } catch (Exception e) {
            throw new CreateSchemaException("Liquibase migration failed for schema: " + schemaName, e);
        }
    }
}
