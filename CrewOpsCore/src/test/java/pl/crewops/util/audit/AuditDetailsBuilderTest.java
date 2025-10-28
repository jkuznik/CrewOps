package pl.crewops.util.audit;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import pl.crewops.enums.DailyEntryAuditType;
import pl.crewops.model.tenantSchema.DailyEntry;
import pl.crewops.model.tenantSchema.JobPosition;
import pl.crewops.model.tenantSchema.Machine;

@ExtendWith(MockitoExtension.class)
class AuditDetailsBuilderTest {

    private static final UUID EMPLOYEE_ID = UUID.fromString("77777777-7777-7777-7777-777777777777");
    private static final Instant T_OLD = Instant.parse("2025-10-23T08:00:00Z");
    private static final Instant T_NEW = Instant.parse("2025-10-23T09:00:00Z");
    private static final UUID OLD_JOB_ID = UUID.randomUUID();
    private static final UUID NEW_JOB_ID = UUID.randomUUID();

    @Mock
    private DailyEntry oldEntry;

    @Mock
    private DailyEntry newEntry;

    @Mock
    private JobPosition oldJobPosition;

    @Mock
    private JobPosition newJobPosition;

    @Mock
    private Machine oldMachine;

    @Mock
    private Machine newMachine;

    // Używamy prawdziwego ObjectMapper, aby poprawnie konwertować Mapy na JsonNode
    @InjectMocks
    private AuditDetailsBuilder auditDetailsBuilder; // Zostawiamy bez inicjalizacji

    private ObjectMapper configuredObjectMapper;

    // Użyjemy metody setUp do poprawnej konfiguracji
    @BeforeEach
    void setUp() {
        // Inicjalizacja skonfigurowanego ObjectMapper
        configuredObjectMapper = new ObjectMapper();
        configuredObjectMapper.registerModule(new JavaTimeModule()); // TO JEST KLUCZOWY DODATEK!

        // Zastrzyknięcie skonfigurowanego obiektu do testowanej klasy
        auditDetailsBuilder = new AuditDetailsBuilder(configuredObjectMapper);

        // Ustawienie wartości domyślnych, które nie powinny się zmieniać w testach
        when(oldEntry.getStartTime()).thenReturn(T_OLD);
        when(oldEntry.getEndTime()).thenReturn(T_OLD);
        when(newEntry.getStartTime()).thenReturn(T_OLD);
        when(newEntry.getEndTime()).thenReturn(T_OLD);

        // Domyślne mockowanie JobPosition
        when(oldEntry.getJobPosition()).thenReturn(null);
        when(newEntry.getJobPosition()).thenReturn(null);
    }

    // --- SCENARIUSZE NADGODZIN (OVERTIME) ---

    @Test
    void shouldNotIncludeOvertimeInPayload_whenOvertimeValueIsZeroAndUnchanged() {
        // Ustawienie: 0 vs 0
        when(oldEntry.getOvertime()).thenReturn(BigDecimal.ZERO);
        when(newEntry.getOvertime()).thenReturn(BigDecimal.ZERO);

        // Symulacja, że JobPosition też się nie zmienia (jest null vs null)
        when(oldEntry.getJobPosition()).thenReturn(null);
        when(newEntry.getJobPosition()).thenReturn(null);

        // Wykonanie
        JsonNode payload = auditDetailsBuilder.createPayload(
                DailyEntryAuditType.INFORMATION_MODIFIED, oldEntry, newEntry, EMPLOYEE_ID);

        // Weryfikacja
        JsonNode oldValues = payload.get("oldValues");
        JsonNode newValues = payload.get("newValues");

        assertNull(oldValues.get("overtime"), "Overtime 0 vs 0 nie powinno być w oldValues.");
        assertNull(newValues.get("overtime"), "Overtime 0 vs 0 nie powinno być w newValues.");
    }

    @Test
    void shouldNotIncludeOvertimeInPayload_whenOvertimeValueIsNonZeroButUnchanged() {
        // Ustawienie: 2.5 vs 2.5 (ten sam stan, różna instancja)
        BigDecimal nonZero = new BigDecimal("2.5");
        when(oldEntry.getOvertime()).thenReturn(nonZero);
        when(newEntry.getOvertime())
                .thenReturn(
                        nonZero); // Uwaga: w teście są to te same instancje, w runtime mogą być inne, ale compareTo i
        // tak działa

        when(oldEntry.getJobPosition()).thenReturn(null);
        when(newEntry.getJobPosition()).thenReturn(null);

        // Wykonanie
        JsonNode payload = auditDetailsBuilder.createPayload(
                DailyEntryAuditType.INFORMATION_MODIFIED, oldEntry, newEntry, EMPLOYEE_ID);

        // Weryfikacja
        JsonNode oldValues = payload.get("oldValues");
        assertNull(oldValues.get("overtime"), "Overtime 2.5 vs 2.5 nie powinno być w payload.");
    }

    @Test
    void shouldIncludeOvertimeInPayload_whenOvertimeChangesFromNonZeroToZero() {
        // Ustawienie: 2.0 vs 0
        BigDecimal oldNonZero = new BigDecimal("2.0");
        BigDecimal newZero = BigDecimal.ZERO;

        when(oldEntry.getOvertime()).thenReturn(oldNonZero);
        when(newEntry.getOvertime()).thenReturn(newZero);

        when(oldEntry.getJobPosition()).thenReturn(null);
        when(newEntry.getJobPosition()).thenReturn(null);

        // Wykonanie
        JsonNode payload = auditDetailsBuilder.createPayload(
                DailyEntryAuditType.INFORMATION_MODIFIED, oldEntry, newEntry, EMPLOYEE_ID);

        // Weryfikacja
        JsonNode oldValues = payload.get("oldValues");
        JsonNode newValues = payload.get("newValues");

        assertNotNull(oldValues.get("overtime"), "Overtime powinno być w oldValues.");
        assertEquals(
                oldNonZero.stripTrailingZeros(),
                new BigDecimal(oldValues.get("overtime").asText()).stripTrailingZeros(),
                "Stara wartość powinna być 2.0.");

        assertNotNull(newValues.get("overtime"), "Overtime powinno być w newValues.");
        assertEquals(newZero, new BigDecimal(newValues.get("overtime").asText()), "Nowa wartość powinna być 0.");
    }

    // --- SCENARIUSZE STANOWISKA PRACY (JOB POSITION) ---

    @Test
    void shouldNotIncludeJobPositionInPayload_whenJobPositionIsUnchanged() {
        // Ustawienie: Obie encje JobPosition są identyczne (symulujemy, że equals jest na ID)
        when(oldEntry.getJobPosition()).thenReturn(oldJobPosition);
        when(newEntry.getJobPosition()).thenReturn(oldJobPosition);

        when(oldEntry.getOvertime()).thenReturn(BigDecimal.ONE);
        when(newEntry.getOvertime()).thenReturn(BigDecimal.ONE);

        // Wykonanie
        JsonNode payload = auditDetailsBuilder.createPayload(
                DailyEntryAuditType.INFORMATION_MODIFIED, oldEntry, newEntry, EMPLOYEE_ID);

        // Weryfikacja
        JsonNode oldValues = payload.get("oldValues");
        assertNull(oldValues.get("jobPosition"), "JobPosition nie powinno być w payload, gdy jest niezmienione.");
    }

    @Test
    void shouldIncludeJobPositionInPayload_whenJobPositionChangesAndIncludesMachine() {
        // Ustawienie: Zmiana stanowiska z "Brakarz" (bez maszyny) na "Operator" (z maszyną)
        when(oldEntry.getJobPosition()).thenReturn(oldJobPosition);
        when(newEntry.getJobPosition()).thenReturn(newJobPosition);

        // Symulacja starych danych
        when(oldJobPosition.getName()).thenReturn("Brakarz");
        when(oldJobPosition.getMachine()).thenReturn(null);

        // Symulacja nowych danych
        when(newJobPosition.getName()).thenReturn("Operator");
        when(newJobPosition.getMachine()).thenReturn(newMachine);
        when(newMachine.getRegisterNumber()).thenReturn("M-55");

        // Wykonanie
        JsonNode payload = auditDetailsBuilder.createPayload(
                DailyEntryAuditType.INFORMATION_MODIFIED, oldEntry, newEntry, EMPLOYEE_ID);

        // Weryfikacja
        JsonNode oldValues = payload.get("oldValues");
        JsonNode newValues = payload.get("newValues");

        assertEquals(
                "Brakarz", oldValues.get("jobPosition").asText(), "Stara wartość powinna być poprawnie sformatowana.");
        assertEquals(
                "Operator (M-55)",
                newValues.get("jobPosition").asText(),
                "Nowa wartość powinna być poprawnie sformatowana z maszyną.");
    }

    @Test
    void shouldIncludeAllChanges_whenMultipleFieldsAreModified() {
        // Ustawienie: Zmiana StartTime, Overtime (2->0), JobPosition (stary->nowy)
        Instant T_NEW = Instant.parse("2025-10-23T09:00:00Z");

        // Zmiana StartTime
        when(newEntry.getStartTime()).thenReturn(T_NEW);

        // Zmiana Overtime
        when(oldEntry.getOvertime()).thenReturn(new BigDecimal("2.0"));
        when(newEntry.getOvertime()).thenReturn(BigDecimal.ZERO);

        // Zmiana JobPosition
        when(oldEntry.getJobPosition()).thenReturn(oldJobPosition);
        when(newEntry.getJobPosition()).thenReturn(newJobPosition);
        when(oldJobPosition.getName()).thenReturn("Stary");
        when(newJobPosition.getName()).thenReturn("Nowy");
        when(oldJobPosition.getMachine()).thenReturn(null);
        when(newJobPosition.getMachine()).thenReturn(null);

        // Wykonanie
        JsonNode payload = auditDetailsBuilder.createPayload(
                DailyEntryAuditType.INFORMATION_MODIFIED, oldEntry, newEntry, EMPLOYEE_ID);

        // Weryfikacja
        JsonNode oldValues = payload.get("oldValues");
        JsonNode newValues = payload.get("newValues");

        // StartTime
        assertNotNull(newValues.get("startTime"));

        // Overtime
        assertNotNull(newValues.get("overtime"));
        assertEquals("2", oldValues.get("overtime").asText());
        assertEquals("0", newValues.get("overtime").asText());

        // JobPosition
        assertNotNull(newValues.get("jobPosition"));
        assertEquals("Stary", oldValues.get("jobPosition").asText());
        assertEquals("Nowy", newValues.get("jobPosition").asText());
    }

    @Test
    void shouldNotIncludeOvertime_whenOvertimeValueIsZeroAndUnchanged() {
        // Ustawienie: 0 vs 0 (poprawne ignorowanie)
        when(oldEntry.getOvertime()).thenReturn(BigDecimal.ZERO);
        when(newEntry.getOvertime()).thenReturn(BigDecimal.ZERO);

        // Wykonanie
        JsonNode payload = auditDetailsBuilder.createPayload(
                DailyEntryAuditType.INFORMATION_MODIFIED, oldEntry, newEntry, EMPLOYEE_ID);

        // Weryfikacja: Pole nie powinno istnieć
        assertNull(payload.get("oldValues").get("overtime"), "Overtime 0 vs 0 nie powinno być w oldValues.");
    }

    @Test
    void shouldNotIncludeOvertime_whenOvertimeValueIsNonZeroButUnchanged() {
        // Ustawienie: 2.5 vs 2.5 (poprawne ignorowanie)
        BigDecimal value = new BigDecimal("2.5000");
        when(oldEntry.getOvertime()).thenReturn(value);
        when(newEntry.getOvertime())
                .thenReturn(value.setScale(4, BigDecimal.ROUND_UNNECESSARY)); // ta sama wartość numeryczna

        // Wykonanie
        JsonNode payload = auditDetailsBuilder.createPayload(
                DailyEntryAuditType.INFORMATION_MODIFIED, oldEntry, newEntry, EMPLOYEE_ID);

        // Weryfikacja: Pole nie powinno istnieć
        assertNull(payload.get("oldValues").get("overtime"), "Overtime 2.5 vs 2.5 nie powinno być w payload.");
    }

    @Test
    void shouldIncludeOvertime_whenOvertimeChangesFromNonZeroToZero() {
        // Ustawienie: 2.0 vs 0 (istotna zmiana)
        BigDecimal oldNonZero = new BigDecimal("2.5");
        BigDecimal newZero = BigDecimal.ZERO;

        when(oldEntry.getOvertime()).thenReturn(oldNonZero);
        when(newEntry.getOvertime()).thenReturn(newZero);

        // Wykonanie
        JsonNode payload = auditDetailsBuilder.createPayload(
                DailyEntryAuditType.INFORMATION_MODIFIED, oldEntry, newEntry, EMPLOYEE_ID);

        // Weryfikacja
        JsonNode oldValues = payload.get("oldValues");
        JsonNode newValues = payload.get("newValues");

        // Wartość powinna być poprawnie zarejestrowana
        assertEquals("2.5", oldValues.get("overtime").asText(), "Stara wartość powinna być 2.5");
        assertEquals("0", newValues.get("overtime").asText(), "Nowa wartość powinna być 0.");
    }

    @Test
    void shouldIncludeOvertime_whenOvertimeChangesFromZeroToNonZero() {
        // Ustawienie: 0 vs 1.5 (istotna zmiana)
        BigDecimal oldZero = BigDecimal.ZERO;
        BigDecimal newNonZero = new BigDecimal("1.5");

        when(oldEntry.getOvertime()).thenReturn(oldZero);
        when(newEntry.getOvertime()).thenReturn(newNonZero);

        // Wykonanie
        JsonNode payload = auditDetailsBuilder.createPayload(
                DailyEntryAuditType.INFORMATION_MODIFIED, oldEntry, newEntry, EMPLOYEE_ID);

        // Weryfikacja
        JsonNode newValues = payload.get("newValues");

        // Weryfikujemy tylko, że zmiana jest widoczna
        assertEquals("1.5", newValues.get("overtime").asText(), "Nowa wartość powinna być 1.5.");
    }

    // --- TESTY STANOWISKA PRACY (JOB POSITION) ---

    @Test
    void shouldIncludeJobPosition_whenJobPositionChangesAndIncludesMachine() {
        // Ustawienie: Zmiana stanowiska i pełne formatowanie
        when(oldEntry.getJobPosition()).thenReturn(oldJobPosition);
        when(newEntry.getJobPosition()).thenReturn(newJobPosition);

        // Symulacja starych danych
        when(oldJobPosition.getName()).thenReturn("Stary Operator");
        when(oldJobPosition.getMachine()).thenReturn(oldMachine);
        when(oldMachine.getRegisterNumber()).thenReturn("M-10");

        // Symulacja nowych danych
        when(newJobPosition.getName()).thenReturn("Nowy Operator");
        when(newJobPosition.getMachine()).thenReturn(newMachine);
        when(newMachine.getRegisterNumber()).thenReturn("M-55");

        // Wykonanie
        JsonNode payload = auditDetailsBuilder.createPayload(
                DailyEntryAuditType.INFORMATION_MODIFIED, oldEntry, newEntry, EMPLOYEE_ID);

        // Weryfikacja
        JsonNode oldValues = payload.get("oldValues");
        JsonNode newValues = payload.get("newValues");

        assertEquals(
                "Stary Operator (M-10)",
                oldValues.get("jobPosition").asText(),
                "Stara wartość powinna być poprawnie sformatowana z maszyną.");
        assertEquals(
                "Nowy Operator (M-55)",
                newValues.get("jobPosition").asText(),
                "Nowa wartość powinna być poprawnie sformatowana z maszyną.");
    }

    @Test
    void shouldIncludeJobPosition_whenJobPositionChangesWithoutMachine() {
        // Ustawienie: Zmiana stanowiska bez maszyny
        when(oldEntry.getJobPosition()).thenReturn(oldJobPosition);
        when(newEntry.getJobPosition()).thenReturn(newJobPosition);

        // Symulacja starych danych
        when(oldJobPosition.getName()).thenReturn("Brakarz");
        when(oldJobPosition.getMachine()).thenReturn(null);

        // Symulacja nowych danych
        when(newJobPosition.getName()).thenReturn("Spawacz");
        when(newJobPosition.getMachine()).thenReturn(null);

        // Wykonanie
        JsonNode payload = auditDetailsBuilder.createPayload(
                DailyEntryAuditType.INFORMATION_MODIFIED, oldEntry, newEntry, EMPLOYEE_ID);

        // Weryfikacja
        JsonNode oldValues = payload.get("oldValues");
        JsonNode newValues = payload.get("newValues");

        assertEquals(
                "Brakarz",
                oldValues.get("jobPosition").asText(),
                "Stara wartość powinna być poprawnie sformatowana bez maszyny.");
        assertEquals(
                "Spawacz",
                newValues.get("jobPosition").asText(),
                "Nowa wartość powinna być poprawnie sformatowana bez maszyny.");
    }

    // --- TESTY MIESZANE ---

    @Test
    void shouldIncludeOnlyStartTime_whenOtherFieldsAreUnchanged() {
        // Ustawienie: Zmiana tylko startTime, reszta bez zmian
        when(newEntry.getStartTime()).thenReturn(T_NEW);

        // Overtime: bez zmian (0 vs 0)
        when(oldEntry.getOvertime()).thenReturn(BigDecimal.ZERO);
        when(newEntry.getOvertime()).thenReturn(BigDecimal.ZERO);

        // JobPosition: bez zmian (null vs null)
        when(oldEntry.getJobPosition()).thenReturn(null);
        when(newEntry.getJobPosition()).thenReturn(null);

        // Wykonanie
        JsonNode payload = auditDetailsBuilder.createPayload(
                DailyEntryAuditType.INFORMATION_MODIFIED, oldEntry, newEntry, EMPLOYEE_ID);

        // Weryfikacja
        JsonNode oldValues = payload.get("oldValues");
        JsonNode newValues = payload.get("newValues");

        // StartTime (powinno być)
        assertNotNull(newValues.get("startTime"));

        // Overtime (nie powinno być)
        assertNull(oldValues.get("overtime"));

        // JobPosition (nie powinno być)
        assertNull(oldValues.get("jobPosition"));
    }

    @Test
    void shouldIncludeOnlyEndTime_whenOtherFieldsAreUnchanged() {
        // Ustawienie: Zmiana tylko endTime, reszta bez zmian
        when(newEntry.getEndTime()).thenReturn(T_NEW);

        // Overtime: bez zmian (0 vs 0)
        when(oldEntry.getOvertime()).thenReturn(BigDecimal.ZERO);
        when(newEntry.getOvertime()).thenReturn(BigDecimal.ZERO);

        // JobPosition: bez zmian (null vs null)
        when(oldEntry.getJobPosition()).thenReturn(null);
        when(newEntry.getJobPosition()).thenReturn(null);

        // Wykonanie
        JsonNode payload = auditDetailsBuilder.createPayload(
                DailyEntryAuditType.INFORMATION_MODIFIED, oldEntry, newEntry, EMPLOYEE_ID);

        // Weryfikacja
        JsonNode oldValues = payload.get("oldValues");
        JsonNode newValues = payload.get("newValues");

        // EndTime (powinno być)
        assertNotNull(newValues.get("endTime"));
        assertEquals(T_NEW.toString(), newValues.get("endTime").asText());

        // StartTime, Overtime, JobPosition (nie powinny być)
        assertNull(oldValues.get("startTime"));
        assertNull(oldValues.get("overtime"));
        assertNull(oldValues.get("jobPosition"));
    }

    // --- NOWE TESTY DLA SCENARIUSZY SKRAJNYCH (null, initial, skala) ---

    @Test
    void shouldIncludeOvertime_whenOvertimeChangesFromNullToNonZero() {
        // Ustawienie: null vs 2.5 (pierwsze wprowadzenie wartości)
        BigDecimal newNonZero = new BigDecimal("2.5");

        when(oldEntry.getOvertime()).thenReturn(null);
        when(newEntry.getOvertime()).thenReturn(newNonZero);
        when(oldEntry.getJobPosition()).thenReturn(null);
        when(newEntry.getJobPosition()).thenReturn(null);

        // Wykonanie
        JsonNode payload = auditDetailsBuilder.createPayload(
                DailyEntryAuditType.INFORMATION_MODIFIED, oldEntry, newEntry, EMPLOYEE_ID);

        // Weryfikacja
        JsonNode oldValues = payload.get("oldValues");
        JsonNode newValues = payload.get("newValues");

        // Overtime
        assertTrue(oldValues.get("overtime").isNull(), "Stara wartość Overtime powinna być null.");
        assertEquals("2.5", newValues.get("overtime").asText(), "Nowa wartość powinna być 2.5.");
    }

    @Test
    void shouldIncludeOvertime_whenOvertimeChangesFromNonZeroToNull() {
        // Ustawienie: 2.5 vs null (usunięcie wartości)
        BigDecimal oldNonZero = new BigDecimal("2.5");

        when(oldEntry.getOvertime()).thenReturn(oldNonZero);
        when(newEntry.getOvertime()).thenReturn(null);
        when(oldEntry.getJobPosition()).thenReturn(null);
        when(newEntry.getJobPosition()).thenReturn(null);

        // Wykonanie
        JsonNode payload = auditDetailsBuilder.createPayload(
                DailyEntryAuditType.INFORMATION_MODIFIED, oldEntry, newEntry, EMPLOYEE_ID);

        // Weryfikacja
        JsonNode oldValues = payload.get("oldValues");
        JsonNode newValues = payload.get("newValues");

        // Overtime
        assertEquals("2.5", oldValues.get("overtime").asText(), "Stara wartość powinna być 2.5.");
        assertTrue(newValues.get("overtime").isNull(), "Nowa wartość Overtime powinna być null.");
    }
}
