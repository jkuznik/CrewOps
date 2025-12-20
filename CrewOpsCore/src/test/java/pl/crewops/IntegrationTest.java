package pl.crewops;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityManager;
import java.util.UUID;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.ApplicationContext;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.transaction.BeforeTransaction;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;
import pl.crewops.domain.auth.AuthAPI;
import pl.crewops.domain.department.DepartmentAPI;
import pl.crewops.domain.employee.EmployeeAPI;
import pl.crewops.domain.machine.MachineAPI;
import pl.crewops.domain.machineType.MachineTypeAPI;
import pl.crewops.domain.message.MessageAPI;
import pl.crewops.domain.note.NoteAPI;
import pl.crewops.domain.qualification.QualificationAPI;
import pl.crewops.domain.scheduleTemplate.ScheduleAPI;
import pl.crewops.domain.tenant.TenantAPI;
import pl.crewops.infrastructure.multitenancy.TenantContext;
import pl.crewops.util.multitenancy.LiquibaseSchemaMigrator;
import pl.crewops.util.multitenancy.SchemaManager;
import pl.crewops.util.spring.SpringContextBridge;

@ExtendWith(SpringExtension.class)
@AutoConfigureMockMvc
@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        properties = "spring.profiles.active=integration")
@Testcontainers
public abstract class IntegrationTest {

    public static final String TESTCONTAINER_DB_NAME = "testdb";
    public static final String TESTCONTAINER_DB_USERNAME = "testUsername";
    public static final String TESTCONTAINER_DB_PASSWORD = "testPassword";
    public static final String TEST_SCHEMA_NAME = "testtenant_2f3b1d5c9e8f";
    public static final UUID TEST_TENANT_COMPANY_ID = UUID.fromString("2f3b1d5c-9e8f-4bca-9c56-123456789abd");
    // tenant with this relation is available in db by test values insertions

    public static final PostgreSQLContainer<?> postgresSQLContainer = new PostgreSQLContainer<>(
                    DockerImageName.parse("postgis/postgis:16-3.4").asCompatibleSubstituteFor("postgres"))
            .withDatabaseName(TESTCONTAINER_DB_NAME)
            .withUsername(TESTCONTAINER_DB_USERNAME)
            .withPassword(TESTCONTAINER_DB_PASSWORD)
            .withReuse(true);

    @Autowired
    protected MockMvc mockMvc;

    @Autowired
    protected ObjectMapper objectMapper;

    @Autowired
    protected TestRestTemplate restTemplate;

    @Autowired
    protected EntityManager entityManager;

    @Autowired
    private ApplicationContext applicationContext;

    @Autowired
    protected AuthAPI authAPI;

    @Autowired
    protected EmployeeAPI employeeAPI;

    @Autowired
    protected DepartmentAPI departmentAPI;

    @Autowired
    protected MachineAPI machineAPI;

    @Autowired
    protected MessageAPI messageAPI;

    @Autowired
    protected MachineTypeAPI machineTypeAPI;

    @Autowired
    protected NoteAPI noteAPI;

    @Autowired
    protected TenantAPI tenantAPI;

    @Autowired
    protected ScheduleAPI scheduleAPI;

    @Autowired
    protected QualificationAPI qualificationAPI;

    @MockitoBean
    protected JavaMailSender mailSender;

    @Autowired
    private LiquibaseSchemaMigrator schemaMigrator;

    @Autowired
    private SchemaManager schemaInitializer;

    @LocalServerPort
    protected int port;

    @BeforeAll
    public static void startContainer() {
        postgresSQLContainer.start();
    }

    @BeforeEach
    public void setupTenantSchema() {
        TenantContext.setCurrentTenant(TEST_SCHEMA_NAME);
        schemaInitializer.createSchemaIfNotExists(TEST_SCHEMA_NAME);
        schemaMigrator.runMigrations(TEST_SCHEMA_NAME);

        SpringContextBridge.setApplicationContext(applicationContext);
    }

    @BeforeTransaction
    public void setUpTenant() {
        TenantContext.setCurrentTenant(TEST_SCHEMA_NAME);
    }

    @DynamicPropertySource
    public static void setTestContainerProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgresSQLContainer::getJdbcUrl);
        registry.add("spring.datasource.username", postgresSQLContainer::getUsername);
        registry.add("spring.datasource.password", postgresSQLContainer::getPassword);
    }

    protected String getBaseUrl() {
        return "http://localhost:" + port;
    }
}
