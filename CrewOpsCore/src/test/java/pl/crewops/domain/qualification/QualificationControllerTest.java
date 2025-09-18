package pl.crewops.domain.qualification;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.*;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import pl.crewops.enums.ControllerURL;
import pl.crewops.model.dto.qualification.CreateQualificationDTO;
import pl.crewops.model.dto.qualification.QualificationDTO;
import pl.crewops.model.dto.qualification.UpdateQualificationDTO;
import pl.crewops.security.config.TestSecuriityConfig;

@ActiveProfiles("test")
@ExtendWith(SpringExtension.class)
@WebMvcTest
@ContextConfiguration(classes = {TestSecuriityConfig.class, QualificationController.class})
class QualificationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private QualificationAPI qualificationAPI;

    @Test
    @DisplayName("POST /qualifications should return 401 when unauthenticated")
    void createQualification_ShouldReturn401() throws Exception {
        CreateQualificationDTO request = new CreateQualificationDTO("New Skill");

        mockMvc.perform(post("/qualifications")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(roles = "MECHANIC")
    @DisplayName("POST /qualifications should return 403 when role is not allowed")
    void createQualification_ShouldReturn403() throws Exception {
        CreateQualificationDTO request = new CreateQualificationDTO("Some Skill");

        mockMvc.perform(post("/qualifications")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "MANAGER")
    @DisplayName("POST /qualifications should allow MANAGER role")
    void createQualification_ShouldAllowManager() throws Exception {
        CreateQualificationDTO request = new CreateQualificationDTO("New Skill");
        QualificationDTO response = QualificationDTO.builder()
                .id(UUID.randomUUID())
                .description("New Skill")
                .build();

        when(qualificationAPI.createQualification(any(CreateQualificationDTO.class)))
                .thenReturn(response);

        mockMvc.perform(post("/qualifications")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.description").value("New Skill"));
    }

    @Test
    @DisplayName("GET /qualifications should return 401 when unauthenticated")
    void getQualifications_ShouldReturn401() throws Exception {
        mockMvc.perform(get("/qualifications")).andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser
    @DisplayName("GET /qualifications should return 200 with list")
    void getQualifications_ShouldReturn200() throws Exception {
        when(qualificationAPI.getAllQualifications(0, 15))
                .thenReturn(List.of(QualificationDTO.builder().build()));

        mockMvc.perform(get("/qualifications"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }

    @Test
    @DisplayName("POST /qualifications/ids should return 401 when unauthenticated")
    void getQualificationsByIds_ShouldReturn401() throws Exception {
        Set<UUID> ids = Set.of(UUID.randomUUID());

        mockMvc.perform(post("/qualifications/ids")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(ids)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser
    @DisplayName("POST /qualifications/ids should return 200 with qualifications")
    void getQualificationsByIds_ShouldReturn200() throws Exception {
        Set<UUID> ids = Set.of(UUID.randomUUID());
        when(qualificationAPI.getQualificationsIn(any(Set.class)))
                .thenReturn(List.of(QualificationDTO.builder().build()));

        mockMvc.perform(post(ControllerURL.QUALIFICATIONS_QIDS)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(ids)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }

    @Test
    @DisplayName("PATCH /qualifications/{id} should return 401 when unauthenticated")
    void updateQualification_ShouldReturn401() throws Exception {
        UUID id = UUID.randomUUID();
        UpdateQualificationDTO request = new UpdateQualificationDTO(id, "Updated");

        mockMvc.perform(patch("/qualifications/" + id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(roles = "MECHANIC")
    @DisplayName("PATCH /qualifications/{id} should return 403 for insufficient role")
    void updateQualification_ShouldReturn403() throws Exception {
        UUID id = UUID.randomUUID();
        UpdateQualificationDTO request = new UpdateQualificationDTO(id, "Skill");

        mockMvc.perform(patch("/qualifications/" + id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "MANAGER")
    @DisplayName("PATCH /qualifications/{id} should return 400 if path/body ID mismatch")
    void updateQualification_ShouldReturn400OnIdMismatch() throws Exception {
        UUID pathId = UUID.randomUUID();
        UUID bodyId = UUID.randomUUID();
        UpdateQualificationDTO request = new UpdateQualificationDTO(bodyId, "Mismatch");

        mockMvc.perform(patch("/qualifications/" + pathId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(roles = "SYSTEM_ADMIN")
    @DisplayName("PATCH /qualifications/{id} should allow SYSTEM_ADMIN role")
    void updateQualification_ShouldAllowSystemAdmin() throws Exception {
        UUID id = UUID.randomUUID();
        UpdateQualificationDTO request = new UpdateQualificationDTO(id, "Updated");
        QualificationDTO response =
                QualificationDTO.builder().id(id).description("Updated").build();

        when(qualificationAPI.updateQualification(any(UpdateQualificationDTO.class)))
                .thenReturn(response);

        mockMvc.perform(patch("/qualifications/" + id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.description").value("Updated"));
    }

    @Test
    @DisplayName("DELETE /qualifications/{id} should return 401 when unauthenticated")
    void deleteQualification_ShouldReturn401() throws Exception {
        UUID id = UUID.randomUUID();

        mockMvc.perform(delete("/qualifications/" + id)).andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(roles = "MECHANIC")
    @DisplayName("DELETE /qualifications/{id} should return 403 for insufficient role")
    void deleteQualification_ShouldReturn403() throws Exception {
        UUID id = UUID.randomUUID();

        mockMvc.perform(delete("/qualifications/" + id)).andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "COMPANY_ADMIN")
    @DisplayName("DELETE /qualifications/{id} should allow COMPANY_ADMIN role")
    void deleteQualification_ShouldAllowCompanyAdmin() throws Exception {
        UUID id = UUID.randomUUID();
        doNothing().when(qualificationAPI).deleteQualification(id);

        mockMvc.perform(delete("/qualifications/" + id)).andExpect(status().isNoContent());
    }
}
