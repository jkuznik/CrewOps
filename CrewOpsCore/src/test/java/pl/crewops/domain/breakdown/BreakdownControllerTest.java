package pl.crewops.domain.breakdown;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static pl.crewops.enums.ControllerURL.BREAKDOWNS;

import com.fasterxml.jackson.databind.ObjectMapper;
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
import pl.crewops.dto.breakdown.BreakdownDTO;
import pl.crewops.dto.breakdown.CreateBreakdownDTO;
import pl.crewops.dto.breakdown.UpdateBreakdownDTO;
import pl.crewops.dto.vehicle.VehicleDTO;
import pl.crewops.security.config.TestSecuriityConfig;

@ExtendWith(SpringExtension.class)
@WebMvcTest
@ContextConfiguration(classes = {TestSecuriityConfig.class, BreakdownController.class})
class BreakdownControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private BreakdownService breakdownService;

    @Test
    @DisplayName("POST /breakdowns should return 201 and created breakdown")
    @WithMockUser
    void createBreakdown_ShouldReturn201() throws Exception {
        CreateBreakdownDTO request =
                new CreateBreakdownDTO(UUID.randomUUID(), UUID.randomUUID(), "Engine failure", true);

        BreakdownDTO response = BreakdownDTO.builder()
                .id(UUID.randomUUID())
                .description("Engine failure")
                .critical(true)
                .vehicle(VehicleDTO.builder().build())
                .repairedBy(null)
                .reportedBy(null)
                .solved(false)
                .build();

        when(breakdownService.createBreakdown(any(CreateBreakdownDTO.class))).thenReturn(response);

        mockMvc.perform(post(BREAKDOWNS)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.description").value("Engine failure"))
                .andExpect(jsonPath("$.critical").value(true));
    }

    @Test
    @DisplayName("GET /breakdowns should return list of breakdowns")
    @WithMockUser
    void getBreakdowns_ShouldReturnList() throws Exception {
        BreakdownDTO dto = BreakdownDTO.builder()
                .id(UUID.randomUUID())
                .description("Flat tire")
                .critical(false)
                .vehicle(VehicleDTO.builder().build())
                .repairedBy(null)
                .reportedBy(null)
                .solved(false)
                .build();

        when(breakdownService.getAllBreakdowns()).thenReturn(List.of(dto));

        mockMvc.perform(get(BREAKDOWNS))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].description").value("Flat tire"))
                .andExpect(jsonPath("$[0].critical").value(false));
    }

    @Test
    @DisplayName("PATCH /breakdowns/{id} should update and return 200 when role is correct")
    @WithMockUser(roles = "SHIFT_LEADER")
    void updateBreakdown_ShouldReturn200WithCorrectRole() throws Exception {
        UUID id = UUID.randomUUID();

        UpdateBreakdownDTO updateRequest = UpdateBreakdownDTO.builder()
                .breakdownId(id)
                .repairedByEmployeeId(UUID.randomUUID())
                .solved(true)
                .build();

        BreakdownDTO response = BreakdownDTO.builder()
                .id(id)
                .description("Fixed")
                .critical(false)
                .solved(true)
                .vehicle(VehicleDTO.builder().build())
                .repairedBy(null)
                .reportedBy(null)
                .build();

        when(breakdownService.updateBreakdown(any(UpdateBreakdownDTO.class))).thenReturn(response);

        mockMvc.perform(patch(BREAKDOWNS + "/" + id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.solved").value(true));
    }

    @Test
    @DisplayName("PATCH /breakdowns/{id} should return 400 when IDs mismatch")
    @WithMockUser(roles = "SHIFT_LEADER")
    void updateBreakdown_ShouldReturn400WhenIdMismatch() throws Exception {
        UUID pathId = UUID.randomUUID();
        UUID bodyId = UUID.randomUUID();

        UpdateBreakdownDTO updateRequest = UpdateBreakdownDTO.builder()
                .breakdownId(bodyId)
                .repairedByEmployeeId(UUID.randomUUID())
                .solved(true)
                .build();

        mockMvc.perform(patch(BREAKDOWNS + "/" + pathId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("PATCH /breakdowns/{id} should return 403 when role is missing")
    @WithMockUser(
            roles = "MECHANIC") // Not allowed but in the future implementation this case probably will be change then
    // test should detect that and then i will update this
    void updateBreakdown_ShouldReturn403WithWrongRole() throws Exception {
        UUID id = UUID.randomUUID();

        UpdateBreakdownDTO updateRequest = UpdateBreakdownDTO.builder()
                .breakdownId(id)
                .repairedByEmployeeId(UUID.randomUUID())
                .solved(true)
                .build();

        mockMvc.perform(patch(BREAKDOWNS + "/" + id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isForbidden());
    }
}
