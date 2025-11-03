package pl.crewops.domain.dailyEntry;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static pl.crewops.enums.DailyEntryStatus.APPROVED;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import pl.crewops.domain.jobPosition.JobPositionAPI;
import pl.crewops.enums.DailyAttendanceStatus;
import pl.crewops.enums.DailyEntryAuditType;
import pl.crewops.enums.DailyEntryStatus;
import pl.crewops.model.dto.dailyEntry.CreateDailyEntryDTO;
import pl.crewops.model.dto.dailyEntry.DailyEntryDTO;
import pl.crewops.model.dto.dailyEntry.UpdateDailyEntryCommand;
import pl.crewops.model.dto.jobPosition.JobPositionDTO;
import pl.crewops.model.tenantSchema.DailyEntry;
import pl.crewops.model.tenantSchema.DailyEntryAudit;
import pl.crewops.util.audit.AuditDetailsBuilder;

@SpringJUnitConfig(
        classes = {
            DailyEntryService.class,
            AuditDetailsBuilder.class,
            DailyEntryRepository.class,
            DailyEntryAuditRepository.class,
            JobPositionAPI.class
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

    @MockitoBean
    JobPositionAPI jobPositionAPI;

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

        when(auditDetailsBuilder.createPayload(any(DailyEntryAuditType.class), any(), any(), any()))
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
    void shouldCreateDailyEntryWithApprovedStatusAndPresentAttendance() {
        // given
        CreateDailyEntryDTO dto = CreateDailyEntryDTO.builder()
                .employeeId(UUID.randomUUID())
                .entryDate(LocalDate.now())
                .actionByEmployeeId(UUID.randomUUID())
                .attendance(null)
                .status(APPROVED)
                .build();

        // when
        DailyEntryDTO result = dailyEntryService.createDailyEntryManually(dto);

        // then
        assertThat(result).isNotNull();
        assertThat(result.attendance()).isEqualTo(DailyAttendanceStatus.PRESENT);
        assertThat(result.status()).isEqualTo(APPROVED);
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
        when(auditDetailsBuilder.createPayload(any(DailyEntryAuditType.class), any(), any(), any()))
                .thenReturn(expectedPayload);

        // when
        DailyEntryDTO result = dailyEntryService.createDailyEntryManually(dto);

        // then
        assertThat(result).isNotNull();
        assertThat(result.status()).isEqualTo(DailyEntryStatus.PENDING);

        // verify audyt wywołany z odpowiednim typem
        verify(auditDetailsBuilder)
                .createPayload(eq(DailyEntryAuditType.ENTRY_STATUS_CHANGED), isNull(), any(DailyEntry.class), any());
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
                .status(APPROVED)
                .build();

        // when
        DailyEntryDTO result1 = dailyEntryService.createDailyEntryManually(dto1);
        DailyEntryDTO result2 = dailyEntryService.createDailyEntryManually(dto2);

        // then
        assertThat(result1.status()).isEqualTo(DailyEntryStatus.DRAFT);
        assertThat(result2.status()).isEqualTo(APPROVED);
        assertThat(result1.attendance()).isEqualTo(DailyAttendanceStatus.PRESENT);
        assertThat(result2.attendance()).isEqualTo(DailyAttendanceStatus.ABSENT);
    }

    @Test
    void getByEmployeeIdAndEntryDate_shouldReturnDailyEntryDTO_withAuditEvents() {
        // given
        UUID employeeId = UUID.randomUUID();
        LocalDate entryDate = LocalDate.now();

        DailyEntry dailyEntry = DailyEntry.builder()
                .employeeId(employeeId)
                .entryDate(entryDate)
                .attendance(DailyAttendanceStatus.PRESENT)
                .auditEvents(new HashSet<>())
                .status(DailyEntryStatus.DRAFT)
                .build();
        dailyEntry.setId(UUID.randomUUID());

        DailyEntryAudit audit = DailyEntryAudit.builder()
                .dailyEntry(dailyEntry)
                .eventType(DailyEntryAuditType.ENTRY_STATUS_CHANGED)
                .comment("Initial event")
                .build();

        dailyEntry.getAuditEvents().add(audit);

        when(dailyEntryRepository.findByEmployeeIdAndEntryDate(employeeId, entryDate))
                .thenReturn(Optional.of(dailyEntry));

        // when
        DailyEntryDTO result = dailyEntryService.getByEmployeeIdAndEntryDate(employeeId, entryDate);

        // then
        assertThat(result).isNotNull();
        assertThat(result.attendance()).isEqualTo(DailyAttendanceStatus.PRESENT);
        assertThat(result.status()).isEqualTo(DailyEntryStatus.DRAFT);

        assertThat(dailyEntry.getAuditEvents()).hasSize(1);
        assertThat(dailyEntry.getAuditEvents().iterator().next().getComment()).isEqualTo("Initial event");
    }

    @Test
    void updateAttendance_shouldChangeAttendanceAndCreateAudit() {
        // given
        UUID employeeId = UUID.randomUUID();
        LocalDate entryDate = LocalDate.now();
        UUID actionBy = UUID.randomUUID();

        DailyEntry dailyEntry = DailyEntry.builder()
                .employeeId(employeeId)
                .entryDate(entryDate)
                .attendance(DailyAttendanceStatus.ABSENT)
                .status(DailyEntryStatus.DRAFT)
                .build();
        dailyEntry.setId(UUID.randomUUID());

        when(dailyEntryRepository.findByEmployeeIdAndEntryDate(employeeId, entryDate))
                .thenReturn(Optional.of(dailyEntry));
        when(dailyEntryRepository.save(any(DailyEntry.class))).thenAnswer(i -> i.getArgument(0));

        UpdateDailyEntryCommand.UpdateAttendance command = new UpdateDailyEntryCommand.UpdateAttendance(
                employeeId, entryDate, actionBy, DailyAttendanceStatus.PRESENT, "Changed attendance");

        // when
        DailyEntryDTO result = dailyEntryService.updateDailyEntry(command);

        // then
        assertThat(result.attendance()).isEqualTo(DailyAttendanceStatus.PRESENT);
        verify(auditDetailsBuilder)
                .createPayload(eq(DailyEntryAuditType.ATTENDANCE_STATUS_CHANGED), any(), any(), eq(actionBy));
        verify(dailyEntryAuditRepository).save(any(DailyEntryAudit.class));
    }

    @Test
    void updateWorkTime_shouldChangeStartAndEndTimesAndCreateAudit() {
        // given
        UUID employeeId = UUID.randomUUID();
        LocalDate entryDate = LocalDate.now();
        UUID actionBy = UUID.randomUUID();

        DailyEntry dailyEntry = DailyEntry.builder()
                .employeeId(employeeId)
                .entryDate(entryDate)
                .startTime(Instant.parse("2025-01-01T08:00:00Z"))
                .endTime(Instant.parse("2025-01-01T16:00:00Z"))
                .build();
        dailyEntry.setId(UUID.randomUUID());

        when(dailyEntryRepository.findByEmployeeIdAndEntryDate(employeeId, entryDate))
                .thenReturn(Optional.of(dailyEntry));
        when(dailyEntryRepository.save(any(DailyEntry.class))).thenAnswer(i -> i.getArgument(0));

        UpdateDailyEntryCommand.UpdateDailyEntryInformation command =
                new UpdateDailyEntryCommand.UpdateDailyEntryInformation(
                        employeeId,
                        entryDate,
                        actionBy,
                        Instant.parse("2025-01-01T09:00:00Z"),
                        Instant.parse("2025-01-01T17:00:00Z"),
                        null,
                        JobPositionDTO.builder().build(),
                        "");

        // when
        DailyEntryDTO result = dailyEntryService.updateDailyEntry(command);

        // then
        assertThat(result.startTime()).isEqualTo(Instant.parse("2025-01-01T09:00:00Z"));
        assertThat(result.endTime()).isEqualTo(Instant.parse("2025-01-01T17:00:00Z"));
        verify(auditDetailsBuilder)
                .createPayload(eq(DailyEntryAuditType.INFORMATION_MODIFIED), any(), any(), eq(actionBy));
        verify(dailyEntryAuditRepository).save(any(DailyEntryAudit.class));
    }

    @Test
    void changeEntryStatus_shouldUpdateStatusAndCreateAudit() {
        // given
        UUID employeeId = UUID.randomUUID();
        LocalDate entryDate = LocalDate.now();
        UUID actionBy = UUID.randomUUID();

        DailyEntry dailyEntry = DailyEntry.builder()
                .employeeId(employeeId)
                .entryDate(entryDate)
                .status(DailyEntryStatus.DRAFT)
                .build();
        dailyEntry.setId(UUID.randomUUID());

        when(dailyEntryRepository.findByEmployeeIdAndEntryDate(employeeId, entryDate))
                .thenReturn(Optional.of(dailyEntry));
        when(dailyEntryRepository.save(any(DailyEntry.class))).thenAnswer(i -> i.getArgument(0));

        UpdateDailyEntryCommand.ChangeEntryStatus command = new UpdateDailyEntryCommand.ChangeEntryStatus(
                employeeId, entryDate, actionBy, APPROVED, "Approved entry");

        // when
        DailyEntryDTO result = dailyEntryService.updateDailyEntry(command);

        // then
        assertThat(result.status()).isEqualTo(APPROVED);
        verify(auditDetailsBuilder)
                .createPayload(eq(DailyEntryAuditType.ENTRY_STATUS_CHANGED), any(), any(), eq(actionBy));
        verify(dailyEntryAuditRepository).save(any(DailyEntryAudit.class));
    }

    @Test
    void addDailyNote_shouldCreateAuditWithNoteContent() {
        // given
        UUID employeeId = UUID.randomUUID();
        LocalDate entryDate = LocalDate.now();
        UUID actionBy = UUID.randomUUID();

        DailyEntry dailyEntry = DailyEntry.builder()
                .employeeId(employeeId)
                .entryDate(entryDate)
                .status(DailyEntryStatus.DRAFT)
                .build();
        dailyEntry.setId(UUID.randomUUID());

        when(dailyEntryRepository.findByEmployeeIdAndEntryDate(employeeId, entryDate))
                .thenReturn(Optional.of(dailyEntry));
        when(dailyEntryRepository.save(any(DailyEntry.class))).thenAnswer(i -> i.getArgument(0));

        UpdateDailyEntryCommand.AddSafetyNote command = new UpdateDailyEntryCommand.AddSafetyNote(
                employeeId, entryDate, actionBy, "New note", "Added daily note");

        // when
        DailyEntryDTO result = dailyEntryService.updateDailyEntry(command);

        // then
        verify(dailyEntryAuditRepository).save(any(DailyEntryAudit.class));
    }

    @Test
    void shouldRevertApprovedEntryAndCreateExtraAudit_whenSensitiveModificationOccurs() {
        // given
        UUID employeeId = UUID.randomUUID();
        LocalDate entryDate = LocalDate.now();
        UUID actionBy = UUID.randomUUID();

        DailyEntry approvedEntry = DailyEntry.builder()
                .employeeId(employeeId)
                .entryDate(entryDate)
                .status(APPROVED)
                .startTime(Instant.parse("2025-01-01T08:00:00Z"))
                .endTime(Instant.parse("2025-01-01T16:00:00Z"))
                .build();
        approvedEntry.setId(UUID.randomUUID());

        when(dailyEntryRepository.findByEmployeeIdAndEntryDate(employeeId, entryDate))
                .thenReturn(Optional.of(approvedEntry));
        when(dailyEntryRepository.save(any(DailyEntry.class))).thenAnswer(i -> i.getArgument(0));

        DailyEntryService spyService = org.mockito.Mockito.spy(dailyEntryService);
        doReturn(true).when(spyService).sensitiveModification(any(), any());

        UpdateDailyEntryCommand.UpdateDailyEntryInformation command =
                new UpdateDailyEntryCommand.UpdateDailyEntryInformation(
                        employeeId,
                        entryDate,
                        actionBy,
                        Instant.parse("2025-01-01T09:00:00Z"),
                        Instant.parse("2025-01-01T17:00:00Z"),
                        null,
                        JobPositionDTO.builder().build(),
                        "Changed after approval");

        // when
        DailyEntryDTO result = spyService.updateDailyEntry(command);

        // then
        assertThat(result.status()).isEqualTo(DailyEntryStatus.MANUAL_EDITED);

        verify(auditDetailsBuilder)
                .createPayload(eq(DailyEntryAuditType.INFORMATION_MODIFIED), any(), any(), eq(actionBy));

        verify(auditDetailsBuilder)
                .createPayload(eq(DailyEntryAuditType.ENTRY_STATUS_CHANGED), any(), any(), eq(actionBy));

        verify(dailyEntryAuditRepository, times(2)).save(any(DailyEntryAudit.class));

        ArgumentCaptor<DailyEntryAudit> auditCaptor = ArgumentCaptor.forClass(DailyEntryAudit.class);
        verify(dailyEntryAuditRepository, atLeastOnce()).save(auditCaptor.capture());

        boolean containsRevertComment = auditCaptor.getAllValues().stream()
                .anyMatch(a ->
                        "Entry was modified after approval — status reverted to MANUAL_EDITED".equals(a.getComment()));

        assertThat(containsRevertComment).isTrue();
    }

    // --- NOWY TEST DLA approveDailyEntry ---

    @Test
    void shouldApproveDailyEntryAndCreateAudit() {
        // given
        UUID employeeId = UUID.randomUUID();
        LocalDate entryDate = LocalDate.now();
        UUID actionBy = UUID.randomUUID();
        String comment = "Approved by manager";

        DailyEntry draftEntry = DailyEntry.builder()
                .employeeId(employeeId)
                .entryDate(entryDate)
                .status(DailyEntryStatus.DRAFT)
                .overtime(BigDecimal.ZERO.setScale(4))
                .build();
        draftEntry.setId(UUID.randomUUID());

        when(dailyEntryRepository.findByEmployeeIdAndEntryDate(employeeId, entryDate))
                .thenReturn(Optional.of(draftEntry));
        when(dailyEntryRepository.save(any(DailyEntry.class))).thenAnswer(i -> i.getArgument(0));

        UpdateDailyEntryCommand command =
                new UpdateDailyEntryCommand.ChangeEntryStatus(employeeId, entryDate, actionBy, APPROVED, comment);

        // when
        DailyEntryDTO result = dailyEntryService.approveDailyEntry(command);

        // then
        assertThat(result.status()).isEqualTo(APPROVED);

        ArgumentCaptor<DailyEntry> entryCaptor = ArgumentCaptor.forClass(DailyEntry.class);
        verify(dailyEntryRepository).save(entryCaptor.capture());
        assertThat(entryCaptor.getValue().getStatus()).isEqualTo(APPROVED);

        verify(auditDetailsBuilder)
                .createPayload(
                        eq(DailyEntryAuditType.ENTRY_STATUS_CHANGED),
                        any(DailyEntry.class), // oldEntry (clone)
                        any(DailyEntry.class), // savedEntry
                        eq(actionBy));

        ArgumentCaptor<DailyEntryAudit> auditCaptor = ArgumentCaptor.forClass(DailyEntryAudit.class);
        verify(dailyEntryAuditRepository).save(auditCaptor.capture());

        DailyEntryAudit savedAudit = auditCaptor.getValue();
        assertThat(savedAudit.getEventType()).isEqualTo(DailyEntryAuditType.ENTRY_STATUS_CHANGED);
        assertThat(savedAudit.getComment()).isEqualTo(comment);
        assertThat(savedAudit.getActionByEmployeeId()).isEqualTo(actionBy);
    }
}
