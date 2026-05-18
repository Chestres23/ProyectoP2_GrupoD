package ec.edu.espe.backend.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import ec.edu.espe.backend.dto.ClaimRequestDTO;
import ec.edu.espe.backend.dto.ClaimResponseDTO;
import ec.edu.espe.backend.service.ClaimService;
import ec.edu.espe.backend.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = ClaimController.class)
class ClaimControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @MockBean
    ClaimService claimService;

    @MockBean
    UserService userService;

    @Test
    @WithMockUser(username = "user", roles = {"USER"})
    void createClaim_asUser_returnsCreated() throws Exception {
        ClaimRequestDTO req = new ClaimRequestDTO();
        req.setItemId(1L);
        req.setObservation("Mi objeto");

        ClaimResponseDTO resp = new ClaimResponseDTO();
        // use reflection-like setters via fields not available; instead build via constructor pattern not present
        // so we'll mock service to return a ClaimResponseDTO with id by stubbing from map in service
        when(claimService.create(any(ClaimRequestDTO.class), any(Long.class))).thenReturn(new ClaimResponseDTO(){
            { try { java.lang.reflect.Field f = this.getClass().getDeclaredField("id"); } catch(Exception e) {} }
            public Long getId(){ return 100L; }
            public java.time.LocalDateTime getClaimDate(){ return LocalDateTime.now(); }
            public String getObservation(){ return "Mi objeto"; }
            public Object getStatus(){ return null; }
            public Long getUserId(){ return 1L; }
            public Long getItemId(){ return 1L; }
        });

        mockMvc.perform(post("/claims")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "/claims/100"));
    }

    @Test
    @WithMockUser(username = "user", roles = {"USER"})
    void listClaims_asUser_returnsOk() throws Exception {
        when(claimService.findAll()).thenReturn(List.of());

        mockMvc.perform(get("/claims"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void approveClaim_asAdmin_returnsNoContent() throws Exception {
        doNothing().when(claimService).approve(eq(5L));

        mockMvc.perform(patch("/claims/5/approve"))
                .andExpect(status().isNoContent());
    }

    @Test
    @WithMockUser(username = "user", roles = {"USER"})
    void approveClaim_asUser_forbidden() throws Exception {
        mockMvc.perform(patch("/claims/5/approve"))
                .andExpect(status().isForbidden());
    }
}
