package pl.crewops;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.transaction.BeforeTransaction;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;
import pl.crewops.infrastructure.multitenancy.TenantContext;
import pl.crewops.utils.multitenancy.LiquibaseSchemaMigrator;
import pl.crewops.utils.multitenancy.TenantSchemaInitializer;

@ExtendWith(SpringExtension.class)
@AutoConfigureMockMvc
@SpringBootTest(properties = "spring.profiles.active=integration")
@Testcontainers
public abstract class IntegrationTest {

    public static final String TESTCONTAINER_DB_NAME = "testdb";
    public static final String TESTCONTAINER_DB_USERNAME = "testUsername";
    public static final String TESTCONTAINER_DB_PASSWORD = "testPassword";
    public static final String TEST_TENANT = "tenant_test";

    public static final PostgreSQLContainer<?> postgresSQLContainer = new PostgreSQLContainer<>(
                    DockerImageName.parse("postgis/postgis:16-3.4").asCompatibleSubstituteFor("postgres"))
            .withDatabaseName(TESTCONTAINER_DB_NAME)
            .withUsername(TESTCONTAINER_DB_USERNAME)
            .withPassword(TESTCONTAINER_DB_PASSWORD)
            .withReuse(true);

    @Autowired
    protected MockMvc mockMvc;

    @Autowired
    private LiquibaseSchemaMigrator schemaMigrator;

    @Autowired
    private TenantSchemaInitializer schemaInitializer;

    @BeforeAll
    public static void startContainer() {
        postgresSQLContainer.start();
    }

    @DynamicPropertySource
    public static void setTestContainerProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgresSQLContainer::getJdbcUrl);
        registry.add("spring.datasource.username", postgresSQLContainer::getUsername);
        registry.add("spring.datasource.password", postgresSQLContainer::getPassword);
    }

    @BeforeTransaction
    public void setUpTenant() {
        TenantContext.setCurrentTenant(TEST_TENANT);
    }

    @BeforeEach
    public void setupTenantSchema() {
        schemaInitializer.createSchemaIfNotExists(TEST_TENANT);
        TenantContext.setCurrentTenant(TEST_TENANT);
        schemaMigrator.runMigrations(TEST_TENANT);
    }
}
