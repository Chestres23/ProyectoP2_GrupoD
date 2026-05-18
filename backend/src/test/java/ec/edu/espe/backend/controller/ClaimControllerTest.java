package ec.edu.espe.backend.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import ec.edu.espe.backend.dto.ClaimRequestDTO;
import ec.edu.espe.backend.dto.ClaimResponseDTO;
import ec.edu.espe.backend.service.ClaimService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
public class ClaimControllerTest {

    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Mock
    private ClaimService claimService;

    @InjectMocks
    private ClaimController claimController;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(claimController).build();
    }

    @Test
    void shouldCreateClaimAndReturnCreatedStatus() throws Exception {
        ClaimRequestDTO request = new ClaimRequestDTO();
        request.setUserId(1L);
        request.setItemId(2L);
        request.setObservation("Reclamo en prueba");

        ClaimResponseDTO response = new ClaimResponseDTO();
        response.setId(10L);
        response.setStatus(null);
        response.setObservation("Reclamo en prueba");
        response.setUserId(1L);
        response.setItemId(2L);

        given(claimService.createClaim(any(ClaimRequestDTO.class))).willReturn(response);

        mockMvc.perform(post("/claims")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(10L))
                .andExpect(jsonPath("$.observation").value("Reclamo en prueba"));
    }

    @Test
    void shouldReturnListOfClaims() throws Exception {
        ClaimResponseDTO response = new ClaimResponseDTO();
        response.setId(10L);
        response.setStatus(null);
        response.setObservation("Reclamo en lista");

        given(claimService.findAll()).willReturn(List.of(response));

        mockMvc.perform(get("/claims"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id").value(10L));
    }
}
