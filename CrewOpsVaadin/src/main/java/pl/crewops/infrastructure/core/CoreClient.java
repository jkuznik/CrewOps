package pl.crewops.infrastructure.core;

import static pl.crewops.enums.ControllerURL.*;
import static pl.crewops.util.CacheResolver.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;
import pl.crewops.exceptions.NotAuthenticatedException;
import pl.crewops.model.dto.breakdown.BreakdownDTO;
import pl.crewops.model.dto.breakdown.CreateBreakdownDTO;
import pl.crewops.model.dto.breakdown.UpdateBreakdownDTO;
import pl.crewops.model.dto.company.CompanyDTO;
import pl.crewops.model.dto.dailyEntry.CreateDailyEntryDTO;
import pl.crewops.model.dto.dailyEntry.DailyEntryDTO;
import pl.crewops.model.dto.dailyEntry.UpdateDailyEntryCommand;
import pl.crewops.model.dto.department.DepartmentDTO;
import pl.crewops.model.dto.employee.EmployeeDTO;
import pl.crewops.model.dto.machine.CreateMachineDTO;
import pl.crewops.model.dto.machine.MachineDTO;
import pl.crewops.model.dto.machine.UpdateMachineDTO;
import pl.crewops.model.dto.machineType.MachineTypeDTO;
import pl.crewops.model.dto.message.MessageDTO;
import pl.crewops.model.dto.message.SendMessageCommand;
import pl.crewops.model.dto.option.AuthUserOptionDTO;
import pl.crewops.model.dto.qualification.CreateQualificationDTO;
import pl.crewops.model.dto.qualification.QualificationDTO;
import pl.crewops.model.dto.qualification.UpdateQualificationDTO;
import pl.crewops.model.dto.qualification.UpdateQualificationExpiredAtDTO;

@Slf4j
@Getter
class CoreClient {

    private final RestClient coreClient;
    private final AuthorizationProvider authorizationProvider;

    public CoreClient(RestClient coreClient) {
        this.coreClient = coreClient;
        this.authorizationProvider = new AuthorizationProvider(coreClient);
    }

    public Set<AuthUserOptionDTO> getOptionsByEmployeeId(UUID employeeId) throws NotAuthenticatedException {
        try {
            return authorizedClient()
                    .get()
                    .uri(uriBuilder -> uriBuilder.path(EMPLOYEE_EID_OPTIONS).build(employeeId))
                    .retrieve()
                    .body(new ParameterizedTypeReference<>() {});
        } catch (RestClientException e) {
            log.error("Get options by employee failed");
            return Set.of();
        }
    }

    // authenticated
    public DailyEntryDTO createDailyEntry(CreateDailyEntryDTO createDailyEntryDTO) throws NotAuthenticatedException {
        try {
            return authorizedClient()
                    .post()
                    .uri(uriBuilder -> uriBuilder.path(DAILY_ENTRIES).build())
                    .body(createDailyEntryDTO)
                    .accept(MediaType.APPLICATION_JSON)
                    .retrieve()
                    .body(new ParameterizedTypeReference<>() {});
        } catch (RestClientException e) {
            log.error("Create new daily entry error");
            return null;
        }
    }

    // authenticated but regular user can fetch only his own daily entry
    // todo: implement cache and security
    public DailyEntryDTO findDailyEntryByEmployeeIdAndDate(UUID employeeId, LocalDate localDate)
            throws NotAuthenticatedException {
        try {
            return authorizedClient()
                    .get()
                    .uri(uriBuilder -> uriBuilder
                            .path(DAILY_ENTRIES)
                            .queryParam("employeeId", employeeId)
                            .queryParam("entryDate", localDate)
                            .build())
                    .retrieve()
                    .body(new ParameterizedTypeReference<>() {});
        } catch (RestClientException e) {
            log.warn("Get daily entry by employee id and entry date failed");
            return null;
        }
    }

    // authenticated and only self resources (for managers should be dedicated method)
    public DailyEntryDTO updateDailyEntrySelfPermission(UpdateDailyEntryCommand updateDailyEntryCommand)
            throws NotAuthenticatedException {
        try {
            return authorizedClient()
                    .patch()
                    .uri(uriBuilder -> uriBuilder.path(DAILY_ENTRIES).build())
                    .body(updateDailyEntryCommand)
                    .retrieve()
                    .body(new ParameterizedTypeReference<>() {});
        } catch (RestClientException e) {
            log.error("Update daily entry failed");
            return null;
        }
    }

    // manager permission
    @Caching(
            evict = {
                @CacheEvict(
                        value = GET_ALL_QUALIFICATIONS,
                        key = "T(pl.crewops.util.CacheResolver).getCurrentCompanyId()"),
                @CacheEvict(
                        value = GET_ALL_QUALIFICATIONS_WITH_EXPIRATION_TIME,
                        key = "T(pl.crewops.util.CacheResolver).getCurrentCompanyId()"),
                @CacheEvict(value = GET_ALL_EMPLOYEES, key = "T(pl.crewops.util.CacheResolver).getCurrentCompanyId()")
            })
    public QualificationDTO createQualification(CreateQualificationDTO createQualificationDTO)
            throws NotAuthenticatedException {
        try {
            return authorizedClient()
                    .post()
                    .uri(uriBuilder -> uriBuilder.path(QUALIFICATIONS).build())
                    .body(createQualificationDTO)
                    .accept(MediaType.APPLICATION_JSON)
                    .retrieve()
                    .body(new ParameterizedTypeReference<>() {});
        } catch (RestClientException e) {
            log.error("Create new qualification error");
            return null;
        }
    }

    // manager permission
    @Caching(
            evict = {
                @CacheEvict(
                        value = GET_ALL_QUALIFICATIONS,
                        key = "T(pl.crewops.util.CacheResolver).getCurrentCompanyId()"),
                @CacheEvict(
                        value = GET_ALL_QUALIFICATIONS_WITH_EXPIRATION_TIME,
                        key = "T(pl.crewops.util.CacheResolver).getCurrentCompanyId()"),
                @CacheEvict(value = GET_ALL_EMPLOYEES, key = "T(pl.crewops.util.CacheResolver).getCurrentCompanyId()")
            })
    public QualificationDTO updateQualification(UpdateQualificationDTO updateQualificationDTO)
            throws NotAuthenticatedException {
        try {
            return authorizedClient()
                    .patch()
                    .uri(uriBuilder ->
                            uriBuilder.path(QUALIFICATIONS_QID).build(updateQualificationDTO.qualificationId()))
                    .body(updateQualificationDTO)
                    .retrieve()
                    .body(new ParameterizedTypeReference<>() {});
        } catch (RestClientException e) {
            log.error("Update qualification error");
            return null;
        }
    }

    // manager permission
    @Caching(
            evict = {
                @CacheEvict(
                        value = GET_ALL_QUALIFICATIONS,
                        key = "T(pl.crewops.util.CacheResolver).getCurrentCompanyId()"),
                @CacheEvict(
                        value = GET_ALL_QUALIFICATIONS_WITH_EXPIRATION_TIME,
                        key = "T(pl.crewops.util.CacheResolver).getCurrentCompanyId()"),
                @CacheEvict(value = GET_ALL_EMPLOYEES, key = "T(pl.crewops.util.CacheResolver).getCurrentCompanyId()")
            })
    public EmployeeDTO updateQualificationExpireAt(UpdateQualificationExpiredAtDTO updateQualificationExpiredAtDTO)
            throws NotAuthenticatedException {
        try {
            return authorizedClient()
                    .patch()
                    .uri(uriBuilder -> uriBuilder
                            .path(EMPLOYEES_EID_QUALIFICATIONS_QID_EXPIRED)
                            .build(
                                    updateQualificationExpiredAtDTO.employeeId(),
                                    updateQualificationExpiredAtDTO.qualificationId()))
                    .body(updateQualificationExpiredAtDTO)
                    .retrieve()
                    .body(new ParameterizedTypeReference<>() {});
        } catch (RestClientException e) {
            log.error("Update qualification expired at error");
            return null;
        }
    }

    public void sendMessage(SendMessageCommand sendMessageCommand) throws NotAuthenticatedException {
        try {
            authorizedClient()
                    .post()
                    .uri(uriBuilder -> uriBuilder.path(MESSAGES).build())
                    .body(sendMessageCommand)
                    .retrieve()
                    .toBodilessEntity();
        } catch (RestClientException e) {
            log.error("Update qualification expired at error");
        }
    }

    public MessageDTO setMessageReadStatus(UUID messageId, boolean status) throws NotAuthenticatedException {
        try {
            return authorizedClient()
                    .patch()
                    .uri(uriBuilder -> uriBuilder.path(MESSAGES_MID).build(messageId))
                    .body(status)
                    .retrieve()
                    .body(new ParameterizedTypeReference<>() {});
        } catch (RestClientException e) {
            log.error("Update message read status error");
            return null;
        }
    }

    // manager permission
    // todo: consider cache by employee id
    public List<QualificationDTO> getAllQualificationsWithExpirationTimeByEmployeeId(UUID employeeId)
            throws NotAuthenticatedException {
        try {
            return authorizedClient()
                    .get()
                    .uri(uriBuilder ->
                            uriBuilder.path(QUALIFICATIONS_EID_EXPIRED).build(employeeId))
                    .retrieve()
                    .body(new ParameterizedTypeReference<>() {});
        } catch (RestClientException e) {
            log.error("Get all qualifications with expiration time by employee id error");
            return List.of();
        }
    }

    // authenticated
    @Cacheable(cacheNames = GET_ALL_QUALIFICATIONS, key = "T(pl.crewops.util.CacheResolver).getCurrentCompanyId()")
    public List<QualificationDTO> getAllQualifications() throws NotAuthenticatedException {
        log.info("Get all qualifications cache missed");
        try {
            return authorizedClient()
                    .get()
                    .uri(uriBuilder -> uriBuilder.path(QUALIFICATIONS).build())
                    .accept(MediaType.APPLICATION_JSON)
                    .retrieve()
                    .body(new ParameterizedTypeReference<List<QualificationDTO>>() {});
        } catch (RestClientException e) {
            log.error("Error getting qualifications");
            return List.of();
        }
    }

    // manager permission
    @Caching(
            evict = {
                @CacheEvict(
                        value = GET_ALL_QUALIFICATIONS,
                        key = "T(pl.crewops.util.CacheResolver).getCurrentCompanyId()"),
                @CacheEvict(
                        value = GET_ALL_QUALIFICATIONS_WITH_EXPIRATION_TIME,
                        key = "T(pl.crewops.util.CacheResolver).getCurrentCompanyId()"),
                @CacheEvict(value = GET_ALL_EMPLOYEES, key = "T(pl.crewops.util.CacheResolver).getCurrentCompanyId()")
            })
    public void deleteQualification(UUID qualificationId) throws NotAuthenticatedException {
        try {
            authorizedClient()
                    .delete()
                    .uri(uriBuilder -> uriBuilder.path(QUALIFICATIONS_QID).build(qualificationId))
                    .retrieve()
                    .toBodilessEntity();
        } catch (RestClientException e) {
            log.error("Error deleting qualification", e);
        }
    }

    // manager permission
    @Caching(
            evict = {
                @CacheEvict(value = GET_ALL_BREAKDOWNS, key = "T(pl.crewops.util.CacheResolver).getCurrentCompanyId()"),
                @CacheEvict(value = GET_ALL_MACHINES, key = "T(pl.crewops.util.CacheResolver).getCurrentCompanyId()"),
                @CacheEvict(
                        value = GET_ALL_MACHINES_BY_IDS,
                        key = "T(pl.crewops.util.CacheResolver).getCurrentCompanyId()"),
                @CacheEvict(
                        value = GET_ALL_MACHINE_TYPES,
                        key = "T(pl.crewops.util.CacheResolver).getCurrentCompanyId()")
            })
    public MachineDTO createMachine(CreateMachineDTO createMachineDTO) throws NotAuthenticatedException {
        try {
            return authorizedClient()
                    .post()
                    .uri(uriBuilder -> uriBuilder.path(MACHINES).build())
                    .body(createMachineDTO)
                    .accept(MediaType.APPLICATION_JSON)
                    .retrieve()
                    .body(new ParameterizedTypeReference<>() {});
        } catch (RestClientException e) {
            log.error("Create new employee error", e);
            return null;
        }
    }
    // manager permission or mechanic authority?

    // shift leader or mechanic
    @Caching(
            evict = {
                @CacheEvict(value = GET_ALL_BREAKDOWNS, key = "T(pl.crewops.util.CacheResolver).getCurrentCompanyId()"),
                @CacheEvict(value = GET_ALL_MACHINES, key = "T(pl.crewops.util.CacheResolver).getCurrentCompanyId()"),
                @CacheEvict(
                        value = GET_ALL_MACHINES_BY_IDS,
                        key = "T(pl.crewops.util.CacheResolver).getCurrentCompanyId()"),
                @CacheEvict(
                        value = GET_ALL_MACHINE_TYPES,
                        key = "T(pl.crewops.util.CacheResolver).getCurrentCompanyId()")
            })
    public MachineDTO updateMachine(UpdateMachineDTO updateMachineDTO) throws NotAuthenticatedException {
        try {
            return authorizedClient()
                    .patch()
                    .uri(uriBuilder -> uriBuilder.path(MACHINES_VID).build(updateMachineDTO.machineId()))
                    .body(updateMachineDTO)
                    .retrieve()
                    .body(new ParameterizedTypeReference<>() {});
        } catch (RestClientException e) {
            log.error("Update employee error", e);
            return null;
        }
    }

    // authenticated
    @Cacheable(cacheNames = GET_ALL_MACHINES, key = "T(pl.crewops.util.CacheResolver).getCurrentCompanyId()")
    public List<MachineDTO> getAllMachines() throws NotAuthenticatedException {
        log.info("Get all machines cache missed");
        try {
            return authorizedClient()
                    .get()
                    .uri(uriBuilder -> uriBuilder.path(MACHINES).build())
                    .accept(MediaType.APPLICATION_JSON)
                    .retrieve()
                    .body(new ParameterizedTypeReference<List<MachineDTO>>() {});
        } catch (RestClientException e) {
            log.error("Error getting machines");
            return List.of();
        }
    }

    // manager permission
    //    @Cacheable(cacheNames = GET_ALL_MACHINES_BY_IDS, key =
    // "T(pl.crewops.util.CacheResolver).getCurrentCompanyId()")
    //    todo: consider cache by ids
    public List<MachineDTO> getAllEmployeeMachinesByIds(Set<UUID> ids) throws NotAuthenticatedException {
        try {
            return authorizedClient()
                    .post()
                    .uri(uriBuilder -> uriBuilder.path(MACHINES_VIDS).build())
                    .body(ids)
                    .retrieve()
                    .body(new ParameterizedTypeReference<>() {});
        } catch (RestClientException e) {
            log.error("Get all employee machine ids error");
            return List.of();
        }
    }

    // authenticated
    @Cacheable(cacheNames = GET_ALL_MACHINE_TYPES, key = "T(pl.crewops.util.CacheResolver).getCurrentCompanyId()")
    public List<MachineTypeDTO> getAllMachineTypes() throws NotAuthenticatedException {
        log.info("Get all machine types cache missed");
        try {
            return authorizedClient()
                    .get()
                    .uri(uriBuilder -> uriBuilder.path(MACHINE_TYPES).build())
                    .accept(MediaType.APPLICATION_JSON)
                    .retrieve()
                    .body(new ParameterizedTypeReference<List<MachineTypeDTO>>() {});
        } catch (RestClientException e) {
            log.error("Error getting machine types");
            return List.of();
        }
    }

    // manager permission
    @Caching(
            evict = {
                @CacheEvict(value = GET_ALL_BREAKDOWNS, key = "T(pl.crewops.util.CacheResolver).getCurrentCompanyId()"),
                @CacheEvict(value = GET_ALL_MACHINES, key = "T(pl.crewops.util.CacheResolver).getCurrentCompanyId()"),
                @CacheEvict(
                        value = GET_ALL_MACHINES_BY_IDS,
                        key = "T(pl.crewops.util.CacheResolver).getCurrentCompanyId()"),
                @CacheEvict(
                        value = GET_ALL_MACHINE_TYPES,
                        key = "T(pl.crewops.util.CacheResolver).getCurrentCompanyId()")
            })
    public void deleteMachine(UUID machineId) throws NotAuthenticatedException {
        try {
            authorizedClient()
                    .delete()
                    .uri(uriBuilder -> uriBuilder
                            .path(MACHINES_VID.replace("{" + MACHINE_ID + "}", machineId.toString()))
                            .build())
                    .retrieve()
                    .toBodilessEntity();
        } catch (RestClientException e) {
            log.error("Error deleting machine", e);
        }
    }

    // authenticated
    @Caching(
            evict = {
                @CacheEvict(value = GET_ALL_BREAKDOWNS, key = "T(pl.crewops.util.CacheResolver).getCurrentCompanyId()"),
                @CacheEvict(value = GET_ALL_MACHINES, key = "T(pl.crewops.util.CacheResolver).getCurrentCompanyId()"),
                @CacheEvict(
                        value = GET_ALL_MACHINES_BY_IDS,
                        key = "T(pl.crewops.util.CacheResolver).getCurrentCompanyId()"),
                @CacheEvict(
                        value = GET_ALL_MACHINE_TYPES,
                        key = "T(pl.crewops.util.CacheResolver).getCurrentCompanyId()")
            })
    public BreakdownDTO createBreakdown(CreateBreakdownDTO createBreakdownDTO) throws NotAuthenticatedException {
        try {
            return authorizedClient()
                    .post()
                    .uri(uriBuilder -> uriBuilder.path(BREAKDOWNS).build())
                    .body(createBreakdownDTO)
                    .retrieve()
                    .body(new ParameterizedTypeReference<>() {});
        } catch (RestClientException e) {
            log.error("Create new breakdown error");
            return null;
        }
    }

    // shift leader or mechanic
    @Caching(
            evict = {
                @CacheEvict(value = GET_ALL_BREAKDOWNS, key = "T(pl.crewops.util.CacheResolver).getCurrentCompanyId()"),
                @CacheEvict(value = GET_ALL_MACHINES, key = "T(pl.crewops.util.CacheResolver).getCurrentCompanyId()"),
                @CacheEvict(
                        value = GET_ALL_MACHINES_BY_IDS,
                        key = "T(pl.crewops.util.CacheResolver).getCurrentCompanyId()"),
                @CacheEvict(
                        value = GET_ALL_MACHINE_TYPES,
                        key = "T(pl.crewops.util.CacheResolver).getCurrentCompanyId()")
            })
    public BreakdownDTO updateBreakdown(UpdateBreakdownDTO updateBreakdownDTO) throws NotAuthenticatedException {
        try {
            return authorizedClient()
                    .patch()
                    .uri(uriBuilder -> uriBuilder.path(BREAKDOWNS_BID).build((updateBreakdownDTO.breakdownId())))
                    .body(updateBreakdownDTO)
                    .retrieve()
                    .body(new ParameterizedTypeReference<>() {});
        } catch (RestClientException e) {
            log.error("Update breakdown error", e);
            return null;
        }
    }

    // authenticated
    @Cacheable(cacheNames = GET_ALL_BREAKDOWNS, key = "T(pl.crewops.util.CacheResolver).getCurrentCompanyId()")
    public List<BreakdownDTO> getAllBreakdowns() throws NotAuthenticatedException {
        log.info("Get all breakdowns cache missed");
        try {
            return authorizedClient()
                    .get()
                    .uri(uriBuilder -> uriBuilder.path(BREAKDOWNS).build())
                    .accept(MediaType.APPLICATION_JSON)
                    .retrieve()
                    .body(new ParameterizedTypeReference<List<BreakdownDTO>>() {});
        } catch (RestClientException e) {
            log.error("Error getting breakdowns");
            return List.of();
        }
    }

    // authenticated
    @Cacheable(cacheNames = GET_ALL_DEPARTMENTS, key = "T(pl.crewops.util.CacheResolver).getCurrentCompanyId()")
    public List<DepartmentDTO> getAllDepartments() throws NotAuthenticatedException {
        log.info("Get all departments cache missed");
        try {
            return authorizedClient()
                    .get()
                    .uri(uriBuilder -> uriBuilder.path(DEPARTMENTS).build())
                    .accept(MediaType.APPLICATION_JSON)
                    .retrieve()
                    .body(new ParameterizedTypeReference<>() {});
        } catch (RestClientException e) {
            log.error("Error getting departments");
            return List.of();
        }
    }

    // authenticated
    // TODO: consider remove caching of this value or implement different logic
    @Cacheable(cacheNames = GET_COMPANY_BY_ID, key = "#companyId")
    public CompanyDTO getCompanyById(UUID companyId) throws NotAuthenticatedException {
        log.info("Get company by id cache missed");
        try {
            return authorizedClient()
                    .get()
                    .uri(uriBuilder -> uriBuilder.path(COMPANIES_CID).build(companyId))
                    .accept(MediaType.APPLICATION_JSON)
                    .retrieve()
                    .body(new ParameterizedTypeReference<>() {});
        } catch (RestClientException e) {
            log.error("Error getting company by id");
            return null;
        }
    }

    public List<MessageDTO> getMessagesByRecipientEmployeeId(UUID employeeId) throws NotAuthenticatedException {
        try {
            return authorizedClient()
                    .get()
                    .uri(uriBuilder -> uriBuilder.path(MESSAGES_EID).build(employeeId))
                    .retrieve()
                    .body(new ParameterizedTypeReference<>() {});
        } catch (RestClientException e) {
            log.error("Error getting messages by recipient employee");
            return List.of();
        }
    }

    private RestClient authorizedClient() throws NotAuthenticatedException {
        return authorizationProvider.authorizedClient();
    }
}
