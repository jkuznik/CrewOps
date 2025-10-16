package pl.crewops.domain.dailyEntry;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.LocalDate;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import pl.crewops.enums.DailyAttendanceStatus;
import pl.crewops.enums.DailyEntryAuditType;
import pl.crewops.enums.DailyEntryStatus;
import pl.crewops.model.dto.dailyEntry.CreateDailyEntryDTO;
import pl.crewops.model.dto.dailyEntry.DailyEntryDTO;
import pl.crewops.model.tenantSchema.DailyEntry;
import pl.crewops.util.audit.AuditDetailsBuilder;

@SpringJUnitConfig(
        classes = {
            DailyEntryService.class,
            AuditDetailsBuilder.class,
            DailyEntryRepository.class,
            DailyEntryAuditRepository.class
        })
class DailyEntryServiceTest {

    @Autowired
    DailyEntryService dailyEntryService;

    @MockitoBean
    DailyEntryRepository dailyEntryRepository;

    @MockitoBean
    DailyEntryAuditRepository dailyEntryAuditRepository;

    @MockitoBean
    AuditDetailsBuilder auditDetailsBuilder;

    private DailyEntry savedEntry;
    private JsonNode payloadNode;

    @BeforeEach
    void setUp() {
        // Default payloadNode, mockujemy dla wszystkich testów
        payloadNode = new ObjectMapper().createObjectNode();

        when(dailyEntryRepository.save(any(DailyEntry.class))).thenAnswer(invocation -> {
            DailyEntry entry = invocation.getArgument(0);
            entry.setId(UUID.randomUUID());
            return entry;
        });

        when(auditDetailsBuilder.createPayload(any(DailyEntryAuditType.class), any(), any()))
                .thenReturn(payloadNode);
    }

    @Test
    void shouldCreateDailyEntryWithDraftStatusAndPresentAttendance() {
        // given
        CreateDailyEntryDTO dto = CreateDailyEntryDTO.builder()
                .employeeId(UUID.randomUUID())
                .entryDate(LocalDate.now())
                .actionByEmployeeId(UUID.randomUUID())
                .attendance(DailyAttendanceStatus.PRESENT)
                .status(DailyEntryStatus.DRAFT)
                .build();

        // when
        DailyEntryDTO result = dailyEntryService.createDailyEntryManually(dto);

        // then
        assertThat(result).isNotNull();
        assertThat(result.attendance()).isEqualTo(DailyAttendanceStatus.PRESENT);
        assertThat(result.status()).isEqualTo(DailyEntryStatus.DRAFT);
    }

    @Test
    void shouldCreateDailyEntryWithApprovedStatusAndAbsentAttendance() {
        // given
        CreateDailyEntryDTO dto = CreateDailyEntryDTO.builder()
                .employeeId(UUID.randomUUID())
                .entryDate(LocalDate.now())
                .actionByEmployeeId(UUID.randomUUID())
                .attendance(null)
                .status(DailyEntryStatus.APPROVED)
                .build();

        // when
        DailyEntryDTO result = dailyEntryService.createDailyEntryManually(dto);

        // then
        assertThat(result).isNotNull();
        assertThat(result.attendance()).isNull();
        assertThat(result.status()).isEqualTo(DailyEntryStatus.APPROVED);
    }

    @Test
    void shouldCreateDailyEntryAndGenerateAuditPayload() {
        // given
        CreateDailyEntryDTO dto = CreateDailyEntryDTO.builder()
                .employeeId(UUID.randomUUID())
                .entryDate(LocalDate.now())
                .actionByEmployeeId(UUID.randomUUID())
                .attendance(DailyAttendanceStatus.PRESENT)
                .status(DailyEntryStatus.PENDING)
                .build();

        JsonNode expectedPayload = new ObjectMapper().createObjectNode();
        when(auditDetailsBuilder.createPayload(any(DailyEntryAuditType.class), any(), any()))
                .thenReturn(expectedPayload);

        // when
        DailyEntryDTO result = dailyEntryService.createDailyEntryManually(dto);

        // then
        assertThat(result).isNotNull();
        assertThat(result.status()).isEqualTo(DailyEntryStatus.PENDING);

        // verify audyt wywołany z odpowiednim typem
        verify(auditDetailsBuilder)
                .createPayload(eq(DailyEntryAuditType.ENTRY_STATUS_CHANGED), isNull(), any(DailyEntry.class));
        verify(dailyEntryAuditRepository).save(any());
    }

    @Test
    void shouldCreateMultipleDailyEntriesWithDifferentStatuses() {
        // given
        CreateDailyEntryDTO dto1 = CreateDailyEntryDTO.builder()
                .employeeId(UUID.randomUUID())
                .entryDate(LocalDate.now())
                .actionByEmployeeId(UUID.randomUUID())
                .attendance(DailyAttendanceStatus.PRESENT)
                .status(DailyEntryStatus.DRAFT)
                .build();

        CreateDailyEntryDTO dto2 = CreateDailyEntryDTO.builder()
                .employeeId(UUID.randomUUID())
                .entryDate(LocalDate.now())
                .actionByEmployeeId(UUID.randomUUID())
                .attendance(DailyAttendanceStatus.ABSENT)
                .status(DailyEntryStatus.APPROVED)
                .build();

        // when
        DailyEntryDTO result1 = dailyEntryService.createDailyEntryManually(dto1);
        DailyEntryDTO result2 = dailyEntryService.createDailyEntryManually(dto2);

        // then
        assertThat(result1.status()).isEqualTo(DailyEntryStatus.DRAFT);
        assertThat(result2.status()).isEqualTo(DailyEntryStatus.APPROVED);
        assertThat(result1.attendance()).isEqualTo(DailyAttendanceStatus.PRESENT);
        assertThat(result2.attendance()).isEqualTo(DailyAttendanceStatus.ABSENT);
    }
}
