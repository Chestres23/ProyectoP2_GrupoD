package ec.edu.espe.backend.controller;

import ec.edu.espe.backend.dto.UserResponseDTO;
import ec.edu.espe.backend.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    // Listar todos los usuarios
    @GetMapping
    public Flux<UserResponseDTO> getAll() {
        return userService.findAll().map(UserResponseDTO::from);
    }

    // Obtener usuario por ID
    @GetMapping("/{id}")
    public Mono<UserResponseDTO> getById(@PathVariable Long id) {
        return userService.findById(id).map(UserResponseDTO::from);
    }

    // Desactivar usuario
    @PatchMapping("/{id}/deactivate")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public Mono<Void> deactivate(@PathVariable Long id) {
        return userService.deactivate(id);
    }
}