package pl.crewops.domain.employee;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import pl.crewops.dto.employee.EmployeeDTO;
import pl.crewops.dto.employee.UpdateEmployeeDTO;
import pl.crewops.security.config.TestSecuriityConfig;

@ExtendWith(SpringExtension.class)
@WebMvcTest
@ContextConfiguration(classes = {TestSecuriityConfig.class, EmployeeController.class})
class EmployeeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private EmployeeAPI employeeAPI;

    @Test
    @DisplayName("GET /employees should return 401 when unauthenticated")
    void getEmployees_ShouldReturn401() throws Exception {
        mockMvc.perform(get("/employees")).andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("PATCH /employees/{id} should return 401 when unauthenticated")
    void updateEmployee_ShouldReturn401() throws Exception {
        UUID id = UUID.randomUUID();
        UpdateEmployeeDTO update =
                UpdateEmployeeDTO.builder().employeeId(id).department("HR").build();

        mockMvc.perform(patch("/employees/" + id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(update)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("PATCH /employees/{id} should return 403 when user lacks required role")
    @WithMockUser(roles = "MECHANIC")
    void updateEmployee_ShouldReturn403ForInsufficientRole() throws Exception {
        UUID id = UUID.randomUUID();
        UpdateEmployeeDTO update =
                UpdateEmployeeDTO.builder().employeeId(id).department("Tech").build();

        mockMvc.perform(patch("/employees/" + id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(update)))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("PATCH /employees/{id} should return 400 when path ID ≠ body ID")
    @WithMockUser(roles = "MANAGER")
    void updateEmployee_ShouldReturn400OnIdMismatch() throws Exception {
        UUID pathId = UUID.randomUUID();
        UUID bodyId = UUID.randomUUID(); // mismatch
        UpdateEmployeeDTO update =
                UpdateEmployeeDTO.builder().employeeId(bodyId).department("QA").build();

        mockMvc.perform(patch("/employees/" + pathId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(update)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("PATCH /employees/{id} should allow MANAGER role")
    @WithMockUser(roles = "MANAGER")
    void updateEmployee_ShouldAllowManager() throws Exception {
        testUpdateSuccess();
    }

    @Test
    @DisplayName("PATCH /employees/{id} should allow COMPANY_ADMIN role")
    @WithMockUser(roles = "COMPANY_ADMIN")
    void updateEmployee_ShouldAllowCompanyAdmin() throws Exception {
        testUpdateSuccess();
    }

    @Test
    @DisplayName("PATCH /employees/{id} should allow SYSTEM_ADMIN role")
    @WithMockUser(roles = "SYSTEM_ADMIN")
    void updateEmployee_ShouldAllowSystemAdmin() throws Exception {
        testUpdateSuccess();
    }

    @Test
    @WithMockUser
    @DisplayName("GET /employees should return 200 with data")
    void getEmployees_ShouldReturn200() throws Exception {
        when(employeeAPI.getAllActiveEmployees(0, 15))
                .thenReturn(List.of(EmployeeDTO.builder().build()));

        mockMvc.perform(get("/employees"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }

    @Test
    @WithMockUser
    @DisplayName("GET /employees/{id} should return 200")
    void getEmployeeById_ShouldReturn200() throws Exception {
        UUID id = UUID.randomUUID();
        when(employeeAPI.getEmployeeDTOById(id))
                .thenReturn(EmployeeDTO.builder().id(id).build());

        mockMvc.perform(get("/employees/" + id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id.toString()));
    }

    @Test
    @WithMockUser
    @DisplayName("PATCH /employees/{id}/phone should remove phone and return 200")
    void removePhoneNumber_ShouldSucceed() throws Exception {
        UUID id = UUID.randomUUID();
        when(employeeAPI.removePhoneNumber(id))
                .thenReturn(EmployeeDTO.builder().id(id).build());

        mockMvc.perform(patch("/employees/" + id + "/phone"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id.toString()));
    }

    @Test
    @WithMockUser
    @DisplayName("PATCH /employees/{id}/qualifications/{id} should succeed")
    void addQualification_ShouldReturn200() throws Exception {
        UUID eid = UUID.randomUUID();
        UUID qid = UUID.randomUUID();

        when(employeeAPI.addQualification(eid, qid))
                .thenReturn(EmployeeDTO.builder().id(eid).build());

        mockMvc.perform(patch("/employees/" + eid + "/qualifications/" + qid))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(eid.toString()));
    }

    @Test
    @WithMockUser
    @DisplayName("PATCH /employees/{id}/qualifications/{id}/expired should update expiry date")
    void updateQualificationExpire_ShouldReturn200() throws Exception {
        UUID eid = UUID.randomUUID();
        UUID qid = UUID.randomUUID();
        Instant newDate = Instant.now().plusSeconds(3600);

        when(employeeAPI.updateQualificationExpiredAt(eid, qid, newDate))
                .thenReturn(EmployeeDTO.builder().id(eid).build());

        mockMvc.perform(patch("/employees/" + eid + "/qualifications/" + qid + "/expired")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newDate)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(eid.toString()));
    }

    @Test
    @WithMockUser
    @DisplayName("DELETE /employees/{eid}/qualifications/{qid} should return 204")
    void removeQualification_ShouldReturn204() throws Exception {
        UUID eid = UUID.randomUUID();
        UUID qid = UUID.randomUUID();

        doNothing().when(employeeAPI).removeQualification(eid, qid);

        mockMvc.perform(delete("/employees/" + eid + "/qualifications/" + qid)).andExpect(status().isNoContent());
    }

    @Test
    @WithMockUser
    @DisplayName("PATCH /employees/{eid}/vehicles/{vid} should return 200")
    void addVehicleToEmployee_ShouldReturn200() throws Exception {
        UUID eid = UUID.randomUUID();
        UUID vid = UUID.randomUUID();

        when(employeeAPI.addVehicle(eid, vid))
                .thenReturn(EmployeeDTO.builder().id(eid).build());

        mockMvc.perform(patch("/employees/" + eid + "/vehicles/" + vid))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(eid.toString()));
    }

    @Test
    @WithMockUser
    @DisplayName("DELETE /employees/{eid}/vehicles/{vid} should return 204")
    void removeVehicleFromEmployee_ShouldReturn204() throws Exception {
        UUID eid = UUID.randomUUID();
        UUID vid = UUID.randomUUID();

        doNothing().when(employeeAPI).removeVehicle(eid, vid);

        mockMvc.perform(delete("/employees/" + eid + "/vehicles/" + vid)).andExpect(status().isNoContent());
    }

    private void testUpdateSuccess() throws Exception {
        UUID id = UUID.randomUUID();

        UpdateEmployeeDTO update = UpdateEmployeeDTO.builder()
                .employeeId(id)
                .department("Operations")
                .build();

        EmployeeDTO response =
                EmployeeDTO.builder().id(id).department("Operations").build();

        when(employeeAPI.updateEmployee(any(UpdateEmployeeDTO.class))).thenReturn(response);

        mockMvc.perform(patch("/employees/" + id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(update)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.department").value("Operations"));
    }
}
