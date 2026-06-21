package ec.edu.espe.backend.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import ec.edu.espe.backend.domain.User;
import ec.edu.espe.backend.dto.UserResponseDTO;
import ec.edu.espe.backend.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;
import java.util.Optional;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willDoNothing;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
public class UserControllerTest {

    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Mock
    private UserService userService;

    @InjectMocks
    private UserController userController;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(userController).build();
    }

    @Test
    void shouldReturnAllUsers() throws Exception {
        User user = new User();
        user.setId(1L);
        user.setName("Juan Perez");
        user.setEmail("juan@espe.edu.ec");
        user.setPassword("password");
        user.setPhone("0991234567");
        user.setRole(User.Role.USER);
        user.setActive(true);

        given(userService.findAll()).willReturn(List.of(user));

        mockMvc.perform(get("/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].name").value("Juan Perez"))
                .andExpect(jsonPath("$[0].email").value("juan@espe.edu.ec"));
    }

    @Test
    void shouldReturnUserById() throws Exception {
        User user = new User();
        user.setId(5L);
        user.setName("Maria Lopez");
        user.setEmail("maria@espe.edu.ec");
        user.setPassword("password");
        user.setRole(User.Role.USER);
        user.setActive(true);

        given(userService.findById(eq(5L))).willReturn(Optional.of(user));

        mockMvc.perform(get("/users/5"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Maria Lopez"))
                .andExpect(jsonPath("$.email").value("maria@espe.edu.ec"));
    }

    @Test
    void shouldReturnNotFoundWhenUserDoesNotExist() throws Exception {
        given(userService.findById(eq(999L))).willReturn(Optional.empty());

        mockMvc.perform(get("/users/999"))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldDeactivateUserAndReturnNoContent() throws Exception {
        willDoNothing().given(userService).deactivate(eq(3L));

        mockMvc.perform(patch("/users/3/deactivate"))
                .andExpect(status().isNoContent());
    }
}
