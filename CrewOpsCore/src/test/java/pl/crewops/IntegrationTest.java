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
import pl.crewops.domain.auth.AuthAPI;
import pl.crewops.domain.employee.EmployeeAPI;
import pl.crewops.domain.qualification.QualificationAPI;
import pl.crewops.domain.tenant.TenantAPI;
import pl.crewops.domain.vehicle.VehicleAPI;
import pl.crewops.domain.vehicleType.VehicleTypeAPI;
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
    public static final String TEST_SCHEMA_NAME = "testtenant_2f3b1d5c9e8f";
    public static final String TEST_TENANT_NAME =
            "TestTenant"; // tenant with this name is available in db by test values insertions

    public static final PostgreSQLContainer<?> postgresSQLContainer = new PostgreSQLContainer<>(
                    DockerImageName.parse("postgis/postgis:16-3.4").asCompatibleSubstituteFor("postgres"))
            .withDatabaseName(TESTCONTAINER_DB_NAME)
            .withUsername(TESTCONTAINER_DB_USERNAME)
            .withPassword(TESTCONTAINER_DB_PASSWORD)
            .withReuse(true);

    @Autowired
    protected MockMvc mockMvc;

    @Autowired
    protected AuthAPI authAPI;

    @Autowired
    protected EmployeeAPI employeeAPI;

    @Autowired
    protected QualificationAPI qualificationAPI;

    @Autowired
    protected TenantAPI tenantAPI;

    @Autowired
    protected VehicleAPI vehicleAPI;

    @Autowired
    protected VehicleTypeAPI vehicleTypeAPI;

    @Autowired
    private LiquibaseSchemaMigrator schemaMigrator;

    @Autowired
    private TenantSchemaInitializer schemaInitializer;

    @DynamicPropertySource
    public static void setTestContainerProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgresSQLContainer::getJdbcUrl);
        registry.add("spring.datasource.username", postgresSQLContainer::getUsername);
        registry.add("spring.datasource.password", postgresSQLContainer::getPassword);
    }

    @BeforeAll
    public static void startContainer() {
        postgresSQLContainer.start();
    }

    @BeforeEach
    public void setupTenantSchema() {
        TenantContext.setCurrentTenant(TEST_SCHEMA_NAME);
        schemaInitializer.createSchemaIfNotExists(TEST_SCHEMA_NAME);
        schemaMigrator.runMigrations(TEST_SCHEMA_NAME);
    }

    @BeforeTransaction
    public void setUpTenant() {
        TenantContext.setCurrentTenant(TEST_SCHEMA_NAME);
    }
}
