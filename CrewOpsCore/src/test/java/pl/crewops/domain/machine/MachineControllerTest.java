package pl.crewops.domain.machine;

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
import pl.crewops.dto.machine.CreateMachineDTO;
import pl.crewops.dto.machine.MachineDTO;
import pl.crewops.dto.machine.UpdateMachineDTO;
import pl.crewops.dto.machineType.MachineTypeDTO;
import pl.crewops.enums.ControllerURL;
import pl.crewops.security.config.TestSecuriityConfig;

@ActiveProfiles("test")
@ExtendWith(SpringExtension.class)
@WebMvcTest
@ContextConfiguration(classes = {TestSecuriityConfig.class, MachineController.class})
class MachineControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private MachineAPI machineAPI;

    @Test
    @DisplayName("POST /machines should return 401 when unauthenticated")
    void createMachine_ShouldReturn401_Unauthenticated() throws Exception {
        CreateMachineDTO request = CreateMachineDTO.builder()
                .make("Toyota")
                .model("Corolla")
                .machineType(MachineTypeDTO.builder().build())
                .year(2020)
                .vin("1HGBH41JXMN109186")
                .registerNumber("ABC123")
                .broken(false)
                .build();

        mockMvc.perform(post("/machines")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(roles = "SHIFT_LEADER")
    @DisplayName("POST /machines should return 403 when role is SHIFT_LEADER")
    void createMachine_ShouldReturn403_ShiftLeader() throws Exception {
        CreateMachineDTO request = CreateMachineDTO.builder()
                .make("Toyota")
                .model("Corolla")
                .machineType(MachineTypeDTO.builder().build())
                .year(2020)
                .vin("1HGBH41JXMN109186")
                .registerNumber("ABC123")
                .broken(false)
                .build();

        mockMvc.perform(post("/machines")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "MANAGER")
    @DisplayName("POST /machines should succeed for MANAGER")
    void createMachine_ShouldAllowManager() throws Exception {
        CreateMachineDTO request = CreateMachineDTO.builder()
                .make("Toyota")
                .model("Corolla")
                .machineType(MachineTypeDTO.builder().build())
                .year(2020)
                .vin("1HGBH41JXMN109186")
                .registerNumber("ABC123")
                .broken(false)
                .build();

        MachineDTO response = MachineDTO.builder()
                .id(UUID.randomUUID())
                .registerNumber("PL‑1234")
                .broken(true)
                .build();

        when(machineAPI.createMachine(any(CreateMachineDTO.class))).thenReturn(response);

        mockMvc.perform(post("/machines")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.registerNumber").value("PL‑1234"))
                .andExpect(jsonPath("$.broken").value(true));
    }

    @Test
    @DisplayName("GET /machines should return 401 when unauthenticated")
    void getAllMachines_ShouldReturn401() throws Exception {
        mockMvc.perform(get("/machines")).andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser
    @DisplayName("GET /machines should return 200 and list")
    void getAllMachines_ShouldReturn200() throws Exception {
        when(machineAPI.getAllMachines(0, 15))
                .thenReturn(Collections.singletonList(
                        MachineDTO.builder().id(UUID.randomUUID()).build()));

        mockMvc.perform(get("/machines"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }

    @Test
    @WithMockUser
    @DisplayName("GET /machines/{regNo} should return 200")
    void getMachineByRegNo_ShouldReturn200() throws Exception {
        String regNo = "TEST123";
        MachineDTO dto =
                MachineDTO.builder().id(UUID.randomUUID()).registerNumber(regNo).build();

        when(machineAPI.getMachineByRegistrationNumber(regNo)).thenReturn(dto);

        mockMvc.perform(get("/machines/" + regNo))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.registerNumber").value(regNo));
    }

    @Test
    @DisplayName("POST /machines/ids should return 401 when unauthenticated")
    void getMachinesByIds_ShouldReturn401() throws Exception {
        Set<UUID> ids = Set.of(UUID.randomUUID());

        mockMvc.perform(post("/machines/ids")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(ids)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser
    @DisplayName("POST /machines/ids should return 200 and list")
    void getMachinesByIds_ShouldReturn200() throws Exception {
        Set<UUID> ids = Set.of(UUID.randomUUID());
        when(machineAPI.getMachinesIn(any(Set.class)))
                .thenReturn(Collections.singletonList(
                        MachineDTO.builder().id(ids.iterator().next()).build()));

        mockMvc.perform(post(ControllerURL.MACHINES_VIDS)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(ids)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }

    @Test
    @DisplayName("PATCH /machines/{id} should return 401 when unauthenticated")
    void updateMachine_ShouldReturn401_Unauthenticated() throws Exception {
        UUID id = UUID.randomUUID();
        UpdateMachineDTO req = new UpdateMachineDTO(id, "ABC1", false);

        mockMvc.perform(patch("/machines/" + id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(roles = "MECHANIC")
    @DisplayName("PATCH /machines/{id} should return 200 for MECHANIC")
    void updateMachine_ShouldReturn403_Mechanic() throws Exception {
        UUID id = UUID.randomUUID();
        UpdateMachineDTO req = new UpdateMachineDTO(id, "ABC2", true);

        mockMvc.perform(patch("/machines/" + id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "SHIFT_LEADER")
    @DisplayName("PATCH /machines/{id} should return 400 if path/body mismatch")
    void updateMachine_ShouldReturn400_IdMismatch() throws Exception {
        UUID pathId = UUID.randomUUID();
        UUID bodyId = UUID.randomUUID();
        UpdateMachineDTO req = new UpdateMachineDTO(bodyId, "XYZ", false);

        mockMvc.perform(patch("/machines/" + pathId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(roles = "SYSTEM_ADMIN")
    @DisplayName("PATCH /machines/{id} should allow SYSTEM_ADMIN (as shift leader)")
    void updateMachine_ShouldAllowSystemAdmin() throws Exception {
        UUID id = UUID.randomUUID();
        UpdateMachineDTO req = new UpdateMachineDTO(id, "ABC9", true);
        MachineDTO resp =
                MachineDTO.builder().id(id).registerNumber("ABC9").broken(true).build();

        when(machineAPI.updateMachine(any(UpdateMachineDTO.class))).thenReturn(resp);

        mockMvc.perform(patch("/machines/" + id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.broken").value(true));
    }

    @Test
    @WithMockUser(roles = "COMPANY_ADMIN")
    @DisplayName("PATCH /machines/{id} should allow COMPANY_ADMIN (as shift leader)")
    void updateMachine_ShouldAllowCompanyAdmin() throws Exception {
        UUID id = UUID.randomUUID();
        UpdateMachineDTO req = new UpdateMachineDTO(id, "DEF123", false);
        MachineDTO resp = MachineDTO.builder()
                .id(id)
                .registerNumber("DEF123")
                .broken(false)
                .build();

        when(machineAPI.updateMachine(any(UpdateMachineDTO.class))).thenReturn(resp);

        mockMvc.perform(patch("/machines/" + id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.registerNumber").value("DEF123"));
    }

    @Test
    @DisplayName("DELETE /machines/{id} should return 401 when unauthenticated")
    void deleteMachine_ShouldReturn401() throws Exception {
        UUID id = UUID.randomUUID();

        mockMvc.perform(delete("/machines/" + id)).andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(roles = "SHIFT_LEADER")
    @DisplayName("DELETE /machines/{id} should return 403 when user is SHIFT_LEADER")
    void deleteMachine_ShouldReturn403_ShiftLeader() throws Exception {
        UUID id = UUID.randomUUID();

        mockMvc.perform(delete("/machines/" + id)).andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "MANAGER")
    @DisplayName("DELETE /machines/{id} should allow MANAGER role")
    void deleteMachine_ShouldAllowManager() throws Exception {
        UUID id = UUID.randomUUID();
        doNothing().when(machineAPI).deleteMachine(id);

        mockMvc.perform(delete("/machines/" + id)).andExpect(status().isOk());
    }
}
