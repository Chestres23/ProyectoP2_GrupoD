package ec.edu.espe.backend.controller;

import ec.edu.espe.backend.dto.AuthRequestDTO;
import ec.edu.espe.backend.dto.AuthResponseDTO;
import ec.edu.espe.backend.dto.RegisterRequestDTO;
import ec.edu.espe.backend.service.impl.AuthService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    // POST /api/auth/register
    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<AuthResponseDTO> register(@Valid @RequestBody RegisterRequestDTO request) {
        return authService.register(request);
    }

    // POST /api/auth/login
    @PostMapping("/login")
    public Mono<AuthResponseDTO> login(@Valid @RequestBody AuthRequestDTO request) {
        return authService.login(request);
    }
}