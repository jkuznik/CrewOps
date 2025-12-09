package pl.crewops.domain.jobPosition;

import static org.assertj.core.api.Assertions.assertThat;

import jakarta.validation.ConstraintViolationException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.Optional;
import javax.sql.DataSource;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import pl.crewops.IntegrationTest;
import pl.crewops.model.dto.jobPosition.CreateJobPositionDTO;
import pl.crewops.model.dto.jobPosition.JobPositionDTO;
import pl.crewops.model.dto.jobPosition.UpdateJobPositionDTO;
import pl.crewops.model.tenantSchema.JobPosition;

@Transactional
class JobPositionAPITest extends IntegrationTest {

    @Autowired
    private JobPositionAPI jobPositionAPI;

    @Autowired
    private JobPositionRepository jobPositionRepository;

    @Autowired
    private DataSource dataSource;

    @Test
    void printCurrentSchema() throws SQLException {
        try (Connection conn = dataSource.getConnection();
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery("SELECT current_schema()")) {
            if (rs.next()) {
                System.out.println("Current schema: " + rs.getString(1));
            }
        }
    }

    @Test
    void shouldThrowException_whenCreateJobPositionDTOIsNotValid() {
        // given
        CreateJobPositionDTO invalidDTO =
                CreateJobPositionDTO.builder().name(null).build();

        // when
        Exception result = Assertions.catchException(() -> jobPositionAPI.createJobPosition(invalidDTO));

        // then
        assertThat(result).isNotNull();
        assertThat(result).isExactlyInstanceOf(ConstraintViolationException.class);
    }

    @Test
    void shouldThrowException_whenUpdateJobPositionDTOIsNotValid() {
        // given
        UpdateJobPositionDTO invalidUpdateDTO = UpdateJobPositionDTO.builder()
                .id(null) // brak id powoduje naruszenie @NotNull
                .name(null)
                .build();

        // when
        Exception result = Assertions.catchException(() -> jobPositionAPI.updateJobPosition(invalidUpdateDTO));

        // then
        assertThat(result).isNotNull();
        assertThat(result).isExactlyInstanceOf(ConstraintViolationException.class);
    }

    @Test
    void shouldReturnJobPositionDTO_whenCreateObjectIsValid() {
        // given
        CreateJobPositionDTO createDTO =
                CreateJobPositionDTO.builder().name("Test JobPosition").build();

        // when
        JobPositionDTO result = jobPositionAPI.createJobPosition(createDTO);

        // then
        assertThat(result).isNotNull();
        assertThat(result.name()).isEqualTo("Test JobPosition");
        assertThat(result.id()).isNotNull();

        // verify persisted entity
        Optional<JobPosition> entity = jobPositionRepository.findById(result.id());
        assertThat(entity).isPresent();
        assertThat(entity.get().getName()).isEqualTo("Test JobPosition");
    }

    @Test
    void shouldReturnJobPositionDTO_whenUpdateObjectIsValid() {
        // given
        JobPosition persisted = jobPositionRepository.save(
                JobPosition.builder().name("Old Name").build());
        UpdateJobPositionDTO updateDTO = UpdateJobPositionDTO.builder()
                .id(persisted.getId())
                .name("Updated Name")
                .build();

        // when
        JobPositionDTO result = jobPositionAPI.updateJobPosition(updateDTO);

        // then
        assertThat(result).isNotNull();
        assertThat(result.name()).isEqualTo("Updated Name");

        // verify updated entity
        Optional<JobPosition> entity = jobPositionRepository.findById(persisted.getId());
        assertThat(entity).isPresent();
        assertThat(entity.get().getName()).isEqualTo("Updated Name");
    }

    @Test
    void findById_ShouldReturnOptional_WhenExists() {
        // given
        JobPosition persisted = jobPositionRepository.save(
                JobPosition.builder().name("Position").build());

        // when
        Optional<JobPosition> result = jobPositionAPI.findById(persisted.getId());

        // then
        assertThat(result).isPresent();
        assertThat(result.get().getName()).isEqualTo("Position");
    }

    @Test
    void findByName_ShouldReturnOptional_WhenExists() {
        // given
        JobPosition persisted = jobPositionRepository.save(
                JobPosition.builder().name("PositionName").build());

        // when
        Optional<JobPosition> result = jobPositionAPI.findByName("PositionName");

        // then
        assertThat(result).isPresent();
        assertThat(result.get().getName()).isEqualTo("PositionName");
    }

    @Test
    void getAllJobPositions_ShouldReturnListOfDTOs() {
        // given
        jobPositionRepository.save(JobPosition.builder().name("JP1").build());
        jobPositionRepository.save(JobPosition.builder().name("JP2").build());

        // when
        List<JobPositionDTO> result = jobPositionAPI.getAllJobPositions();

        // then
        assertThat(result).hasSize(2);
        assertThat(result.stream().map(JobPositionDTO::name)).containsExactlyInAnyOrder("JP1", "JP2");
    }

    @Test
    void deleteById_ShouldRemoveJobPosition() {
        // given
        JobPosition persisted = jobPositionRepository.save(
                JobPosition.builder().name("ToDelete").build());

        // when
        jobPositionAPI.deleteById(persisted.getId());

        // then
        Optional<JobPosition> result = jobPositionRepository.findById(persisted.getId());
        assertThat(result).isEmpty();
    }
}
