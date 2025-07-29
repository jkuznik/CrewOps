package pl.crewops.domain.vehicle;

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
import pl.crewops.dto.vehicle.CreateVehicleDTO;
import pl.crewops.dto.vehicle.UpdateVehicleDTO;
import pl.crewops.dto.vehicle.VehicleDTO;
import pl.crewops.dto.vehicleType.VehicleTypeDTO;
import pl.crewops.enums.ControllerURL;
import pl.crewops.security.config.TestSecuriityConfig;

@ActiveProfiles("test")
@ExtendWith(SpringExtension.class)
@WebMvcTest
@ContextConfiguration(classes = {TestSecuriityConfig.class, VehicleController.class})
class VehicleControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private VehicleAPI vehicleAPI;

    @Test
    @DisplayName("POST /vehicles should return 401 when unauthenticated")
    void createVehicle_ShouldReturn401_Unauthenticated() throws Exception {
        CreateVehicleDTO request = CreateVehicleDTO.builder()
                .make("Toyota")
                .model("Corolla")
                .vehicleType(VehicleTypeDTO.builder().build())
                .year(2020)
                .vin("1HGBH41JXMN109186")
                .registerNumber("ABC123")
                .broken(false)
                .build();

        mockMvc.perform(post("/vehicles")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(roles = "SHIFT_LEADER")
    @DisplayName("POST /vehicles should return 403 when role is SHIFT_LEADER")
    void createVehicle_ShouldReturn403_ShiftLeader() throws Exception {
        CreateVehicleDTO request = CreateVehicleDTO.builder()
                .make("Toyota")
                .model("Corolla")
                .vehicleType(VehicleTypeDTO.builder().build())
                .year(2020)
                .vin("1HGBH41JXMN109186")
                .registerNumber("ABC123")
                .broken(false)
                .build();

        mockMvc.perform(post("/vehicles")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "MANAGER")
    @DisplayName("POST /vehicles should succeed for MANAGER")
    void createVehicle_ShouldAllowManager() throws Exception {
        CreateVehicleDTO request = CreateVehicleDTO.builder()
                .make("Toyota")
                .model("Corolla")
                .vehicleType(VehicleTypeDTO.builder().build())
                .year(2020)
                .vin("1HGBH41JXMN109186")
                .registerNumber("ABC123")
                .broken(false)
                .build();

        VehicleDTO response = VehicleDTO.builder()
                .id(UUID.randomUUID())
                .registerNumber("PL‑1234")
                .broken(true)
                .build();

        when(vehicleAPI.createVehicle(any(CreateVehicleDTO.class))).thenReturn(response);

        mockMvc.perform(post("/vehicles")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.registerNumber").value("PL‑1234"))
                .andExpect(jsonPath("$.broken").value(true));
    }

    @Test
    @DisplayName("GET /vehicles should return 401 when unauthenticated")
    void getAllVehicles_ShouldReturn401() throws Exception {
        mockMvc.perform(get("/vehicles")).andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser
    @DisplayName("GET /vehicles should return 200 and list")
    void getAllVehicles_ShouldReturn200() throws Exception {
        when(vehicleAPI.getAllVehicles(0, 15))
                .thenReturn(Collections.singletonList(
                        VehicleDTO.builder().id(UUID.randomUUID()).build()));

        mockMvc.perform(get("/vehicles"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }

    @Test
    @WithMockUser
    @DisplayName("GET /vehicles/{regNo} should return 200")
    void getVehicleByRegNo_ShouldReturn200() throws Exception {
        String regNo = "TEST123";
        VehicleDTO dto =
                VehicleDTO.builder().id(UUID.randomUUID()).registerNumber(regNo).build();

        when(vehicleAPI.getVehicleByRegistrationNumber(regNo)).thenReturn(dto);

        mockMvc.perform(get("/vehicles/" + regNo))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.registerNumber").value(regNo));
    }

    @Test
    @DisplayName("POST /vehicles/ids should return 401 when unauthenticated")
    void getVehiclesByIds_ShouldReturn401() throws Exception {
        Set<UUID> ids = Set.of(UUID.randomUUID());

        mockMvc.perform(post("/vehicles/ids")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(ids)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser
    @DisplayName("POST /vehicles/ids should return 200 and list")
    void getVehiclesByIds_ShouldReturn200() throws Exception {
        Set<UUID> ids = Set.of(UUID.randomUUID());
        when(vehicleAPI.getVehiclesIn(any(Set.class)))
                .thenReturn(Collections.singletonList(
                        VehicleDTO.builder().id(ids.iterator().next()).build()));

        mockMvc.perform(post(ControllerURL.VEHICLES_VIDS)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(ids)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }

    @Test
    @DisplayName("PATCH /vehicles/{id} should return 401 when unauthenticated")
    void updateVehicle_ShouldReturn401_Unauthenticated() throws Exception {
        UUID id = UUID.randomUUID();
        UpdateVehicleDTO req = new UpdateVehicleDTO(id, "ABC1", false);

        mockMvc.perform(patch("/vehicles/" + id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(roles = "MECHANIC")
    @DisplayName("PATCH /vehicles/{id} should return 200 for MECHANIC")
    void updateVehicle_ShouldReturn403_Mechanic() throws Exception {
        UUID id = UUID.randomUUID();
        UpdateVehicleDTO req = new UpdateVehicleDTO(id, "ABC2", true);

        mockMvc.perform(patch("/vehicles/" + id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "SHIFT_LEADER")
    @DisplayName("PATCH /vehicles/{id} should return 400 if path/body mismatch")
    void updateVehicle_ShouldReturn400_IdMismatch() throws Exception {
        UUID pathId = UUID.randomUUID();
        UUID bodyId = UUID.randomUUID();
        UpdateVehicleDTO req = new UpdateVehicleDTO(bodyId, "XYZ", false);

        mockMvc.perform(patch("/vehicles/" + pathId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(roles = "SYSTEM_ADMIN")
    @DisplayName("PATCH /vehicles/{id} should allow SYSTEM_ADMIN (as shift leader)")
    void updateVehicle_ShouldAllowSystemAdmin() throws Exception {
        UUID id = UUID.randomUUID();
        UpdateVehicleDTO req = new UpdateVehicleDTO(id, "ABC9", true);
        VehicleDTO resp =
                VehicleDTO.builder().id(id).registerNumber("ABC9").broken(true).build();

        when(vehicleAPI.updateVehicle(any(UpdateVehicleDTO.class))).thenReturn(resp);

        mockMvc.perform(patch("/vehicles/" + id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.broken").value(true));
    }

    @Test
    @WithMockUser(roles = "COMPANY_ADMIN")
    @DisplayName("PATCH /vehicles/{id} should allow COMPANY_ADMIN (as shift leader)")
    void updateVehicle_ShouldAllowCompanyAdmin() throws Exception {
        UUID id = UUID.randomUUID();
        UpdateVehicleDTO req = new UpdateVehicleDTO(id, "DEF123", false);
        VehicleDTO resp = VehicleDTO.builder()
                .id(id)
                .registerNumber("DEF123")
                .broken(false)
                .build();

        when(vehicleAPI.updateVehicle(any(UpdateVehicleDTO.class))).thenReturn(resp);

        mockMvc.perform(patch("/vehicles/" + id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.registerNumber").value("DEF123"));
    }

    @Test
    @DisplayName("DELETE /vehicles/{id} should return 401 when unauthenticated")
    void deleteVehicle_ShouldReturn401() throws Exception {
        UUID id = UUID.randomUUID();

        mockMvc.perform(delete("/vehicles/" + id)).andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(roles = "SHIFT_LEADER")
    @DisplayName("DELETE /vehicles/{id} should return 403 when user is SHIFT_LEADER")
    void deleteVehicle_ShouldReturn403_ShiftLeader() throws Exception {
        UUID id = UUID.randomUUID();

        mockMvc.perform(delete("/vehicles/" + id)).andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "MANAGER")
    @DisplayName("DELETE /vehicles/{id} should allow MANAGER role")
    void deleteVehicle_ShouldAllowManager() throws Exception {
        UUID id = UUID.randomUUID();
        doNothing().when(vehicleAPI).deleteVehicle(id);

        mockMvc.perform(delete("/vehicles/" + id)).andExpect(status().isOk());
    }
}
