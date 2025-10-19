package pl.crewops.infrastructure.core;

import static pl.crewops.enums.ControllerURL.*;

import java.util.List;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestClientException;
import pl.crewops.exceptions.NotAuthenticatedException;
import pl.crewops.model.dto.auth.CreateAuthUserResult;
import pl.crewops.model.dto.employee.CreateEmployeeDTO;
import pl.crewops.model.dto.employee.EmployeeDTO;
import pl.crewops.model.dto.employee.UpdateEmployeeDTO;

@Slf4j
class DomainEmployeeClient {

    private final AuthorizationProvider authorizationProvider;

    public DomainEmployeeClient(AuthorizationProvider authorizationProvider) {
        this.authorizationProvider = authorizationProvider;
    }

    // manager permission
    public CreateAuthUserResult createEmployee(CreateEmployeeDTO createEmployeeDTO) throws NotAuthenticatedException {
        try {
            return authorizationProvider
                    .authorizedClient()
                    .post()
                    .uri(uriBuilder -> uriBuilder.path(EMPLOYEES).build())
                    .body(createEmployeeDTO)
                    .accept(MediaType.APPLICATION_JSON)
                    .retrieve()
                    .body(new ParameterizedTypeReference<>() {});
        } catch (RestClientException e) {
            log.error("Create new employee error");
            return null;
        }
    }

    // authenticated
    public EmployeeDTO getEmployeeById(UUID employeeId) throws NotAuthenticatedException {
        log.info("Get employee by id cache missed");
        try {
            return authorizationProvider
                    .authorizedClient()
                    .get()
                    .uri(uriBuilder -> uriBuilder.path(EMPLOYEES_EID).build(employeeId))
                    .accept(MediaType.APPLICATION_JSON)
                    .retrieve()
                    .body(new ParameterizedTypeReference<>() {});
        } catch (RestClientException e) {
            log.error("Error getting employee by id");
            return null;
        }
    }

    // authenticated
    public List<EmployeeDTO> getAllEmployees() throws NotAuthenticatedException {
        log.info("Get all employees cache missed");
        try {
            return authorizationProvider
                    .authorizedClient()
                    .get()
                    .uri(uriBuilder -> uriBuilder.path(EMPLOYEES).build())
                    .accept(MediaType.APPLICATION_JSON)
                    .retrieve()
                    .body(new ParameterizedTypeReference<>() {});
        } catch (RestClientException e) {
            log.error("Error getting employees");
            return List.of();
        }
    }

    // manager permission
    public EmployeeDTO updateEmployee(UpdateEmployeeDTO updateEmployeeDTO) throws NotAuthenticatedException {
        try {
            return authorizationProvider
                    .authorizedClient()
                    .patch()
                    .uri(uriBuilder -> uriBuilder.path(EMPLOYEES_EID).build(updateEmployeeDTO.employeeId()))
                    .body(updateEmployeeDTO)
                    .retrieve()
                    .body(new ParameterizedTypeReference<>() {});
        } catch (RestClientException e) {
            log.error("Update employee error");
            return null;
        }
    }

    // self permission
    public EmployeeDTO updateEmployeeSelfProfile(UpdateEmployeeDTO updateEmployeeDTO) throws NotAuthenticatedException {
        try {
            return authorizationProvider
                    .authorizedClient()
                    .put()
                    .uri(uriBuilder -> uriBuilder.path(EMPLOYEES_EID).build(updateEmployeeDTO.employeeId()))
                    .body(updateEmployeeDTO)
                    .retrieve()
                    .body(new ParameterizedTypeReference<>() {});
        } catch (RestClientException e) {
            log.error("Update employee error");
            return null;
        }
    }

    // manager permission
    public EmployeeDTO addEmployeeDepartment(UUID employeeId, UUID departmentId) throws NotAuthenticatedException {
        log.info("Assignment department to employee");
        try {
            return authorizationProvider
                    .authorizedClient()
                    .patch()
                    .uri(uriBuilder -> uriBuilder
                            .path(EMPLOYEES_EID_DEPARTMENTS_DID)
                            .build(employeeId.toString(), departmentId.toString()))
                    .retrieve()
                    .body(new ParameterizedTypeReference<>() {});
        } catch (RestClientException e) {
            log.error("Error during add department to employee");
            return null;
        }
    }

    // manager permission
    public EmployeeDTO addEmployeeQualification(UUID employeeId, UUID qualificationId)
            throws NotAuthenticatedException {
        try {
            return authorizationProvider
                    .authorizedClient()
                    .patch()
                    .uri(uriBuilder -> uriBuilder
                            .path(EMPLOYEES_EID_QUALIFICATIONS_QID)
                            .build(employeeId.toString(), qualificationId.toString()))
                    .retrieve()
                    .body(new ParameterizedTypeReference<>() {});
        } catch (RestClientException e) {
            log.error("Add employee qualification failed");
            return null;
        }
    }

    // manager permission
    public EmployeeDTO addEmployeeMachine(UUID employeeId, UUID machineId) throws NotAuthenticatedException {
        try {
            return authorizationProvider
                    .authorizedClient()
                    .patch()
                    .uri(uriBuilder ->
                            uriBuilder.path(EMPLOYEES_EID_MACHINES_VID).build(employeeId, machineId))
                    .retrieve()
                    .body(new ParameterizedTypeReference<>() {});
        } catch (RestClientException e) {
            log.error("Add employee machine error", e);
            return null;
        }
    }

    // manager permission
    public void removeEmployeeDepartment(UUID employeeId, UUID departmentId) throws NotAuthenticatedException {
        try {
            authorizationProvider
                    .authorizedClient()
                    .delete()
                    .uri(uriBuilder -> uriBuilder
                            .path(EMPLOYEES_EID_DEPARTMENTS_DID)
                            .build(employeeId.toString(), departmentId.toString()))
                    .retrieve()
                    .toBodilessEntity();
        } catch (RestClientException e) {
            log.error("Error during remove department from employee");
        }
    }

    // manager permission
    public void removeEmployeeQualification(UUID employeeId, UUID qualificationId) throws NotAuthenticatedException {
        try {
            authorizationProvider
                    .authorizedClient()
                    .delete()
                    .uri(uriBuilder ->
                            uriBuilder.path(EMPLOYEES_EID_QUALIFICATIONS_QID).build(employeeId, qualificationId))
                    .retrieve()
                    .toBodilessEntity();
        } catch (RestClientException e) {
            log.error("Remove employee qualification error");
        }
    }

    // manager permission
    public void removeEmployeeMachine(UUID employeeId, UUID machineId) throws NotAuthenticatedException {
        try {
            authorizationProvider
                    .authorizedClient()
                    .delete()
                    .uri(uriBuilder ->
                            uriBuilder.path(EMPLOYEES_EID_MACHINES_VID).build(employeeId, machineId))
                    .retrieve()
                    .toBodilessEntity();
        } catch (RestClientException e) {
            log.error("Remove employee machine error");
        }
    }
}
