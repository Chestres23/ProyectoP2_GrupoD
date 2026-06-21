package ec.edu.espe.backend.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import ec.edu.espe.backend.domain.enums.ItemStatus;
import ec.edu.espe.backend.dto.LostItemRequestDTO;
import ec.edu.espe.backend.dto.LostItemResponseDTO;
import ec.edu.espe.backend.service.LostItemService;
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
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willDoNothing;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
public class LostItemControllerTest {

    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Mock
    private LostItemService service;

    @InjectMocks
    private LostItemController lostItemController;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(lostItemController).build();
    }

    @Test
    void shouldCreateItemAndReturnOk() throws Exception {
        LostItemRequestDTO request = new LostItemRequestDTO();
        request.setName("Mochila azul");
        request.setDescription("Mochila encontrada en aula");
        request.setCategory("Bolsos");
        request.setLocationFound("Aula 101");
        request.setUserId(1L);

        LostItemResponseDTO response = new LostItemResponseDTO();
        response.setId(1L);
        response.setName("Mochila azul");
        response.setDescription("Mochila encontrada en aula");
        response.setCategory("Bolsos");
        response.setLocationFound("Aula 101");
        response.setStatus(ItemStatus.FOUND);
        response.setReporterName("Test User");
        response.setReporterId(1L);

        given(service.createItem(any(LostItemRequestDTO.class))).willReturn(response);

        mockMvc.perform(post("/items")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("Mochila azul"))
                .andExpect(jsonPath("$.category").value("Bolsos"));
    }

    @Test
    void shouldReturnAllActiveItems() throws Exception {
        LostItemResponseDTO response = new LostItemResponseDTO();
        response.setId(1L);
        response.setName("Llave USB");
        response.setStatus(ItemStatus.FOUND);
        response.setReporterName("Test User");
        response.setReporterId(1L);

        given(service.getAllActiveItems()).willReturn(List.of(response));

        mockMvc.perform(get("/items"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].name").value("Llave USB"));
    }

    @Test
    void shouldReturnItemById() throws Exception {
        LostItemResponseDTO response = new LostItemResponseDTO();
        response.setId(5L);
        response.setName("Cartera negra");
        response.setCategory("Accesorios");
        response.setStatus(ItemStatus.FOUND);
        response.setReporterName("Test User");
        response.setReporterId(1L);

        given(service.getItemById(eq(5L))).willReturn(response);

        mockMvc.perform(get("/items/5"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(5L))
                .andExpect(jsonPath("$.name").value("Cartera negra"));
    }

    @Test
    void shouldClaimItemAndReturnOk() throws Exception {
        LostItemResponseDTO response = new LostItemResponseDTO();
        response.setId(3L);
        response.setName("Celular Samsung");
        response.setStatus(ItemStatus.CLAIMED);
        response.setReporterName("Test User");
        response.setReporterId(1L);

        given(service.claimItem(eq(3L))).willReturn(response);

        mockMvc.perform(patch("/items/3/claim"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(3L))
                .andExpect(jsonPath("$.status").value("CLAIMED"));
    }

    @Test
    void shouldDeliverItemAndReturnOk() throws Exception {
        LostItemResponseDTO response = new LostItemResponseDTO();
        response.setId(4L);
        response.setName("Laptop HP");
        response.setStatus(ItemStatus.DELIVERED);
        response.setReporterName("Test User");
        response.setReporterId(1L);

        given(service.deliverItem(eq(4L))).willReturn(response);

        mockMvc.perform(patch("/items/4/deliver"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(4L))
                .andExpect(jsonPath("$.status").value("DELIVERED"));
    }

    @Test
    void shouldUpdateItemAndReturnOk() throws Exception {
        LostItemRequestDTO request = new LostItemRequestDTO();
        request.setName("Mochila verde actualizada");
        request.setCategory("Bolsos");

        LostItemResponseDTO response = new LostItemResponseDTO();
        response.setId(2L);
        response.setName("Mochila verde actualizada");
        response.setCategory("Bolsos");
        response.setStatus(ItemStatus.FOUND);
        response.setReporterName("Test User");
        response.setReporterId(1L);

        given(service.updateItem(eq(2L), any(LostItemRequestDTO.class))).willReturn(response);

        mockMvc.perform(put("/items/2")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(2L))
                .andExpect(jsonPath("$.name").value("Mochila verde actualizada"));
    }

    @Test
    void shouldDeleteItemAndReturnNoContent() throws Exception {
        willDoNothing().given(service).deleteItem(eq(6L));

        mockMvc.perform(delete("/items/6"))
                .andExpect(status().isNoContent());
    }

    @Test
    void shouldReturnNotFoundWhenImageDoesNotExist() throws Exception {
        given(service.getImageBytes(eq(10L))).willReturn(null);

        mockMvc.perform(get("/items/10/image"))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldReturnImageWhenExists() throws Exception {
        byte[] imageData = new byte[]{1, 2, 3, 4, 5};

        given(service.getImageBytes(eq(10L))).willReturn(imageData);
        given(service.getImageContentType(eq(10L))).willReturn("image/png");

        mockMvc.perform(get("/items/10/image"))
                .andExpect(status().isOk())
                .andExpect(header().string("Content-Type", "image/png"));
    }
}
