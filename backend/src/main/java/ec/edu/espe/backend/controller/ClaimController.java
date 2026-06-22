package ec.edu.espe.backend.controller;

import ec.edu.espe.backend.dto.ClaimRequestDTO;
import ec.edu.espe.backend.dto.ClaimResponseDTO;
import ec.edu.espe.backend.service.ClaimService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/claims")
public class ClaimController {

    private final ClaimService claimService;

    public ClaimController(ClaimService claimService) {
        this.claimService = claimService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<ClaimResponseDTO> create(@Valid @RequestBody ClaimRequestDTO request) {
        return claimService.createClaim(request);
    }

    @GetMapping
    public Flux<ClaimResponseDTO> getAll() {
        return claimService.findAll();
    }

    @PatchMapping("/{id}/approve")
    public Mono<ClaimResponseDTO> approve(@PathVariable Long id) {
        return claimService.approve(id);
    }

    @PatchMapping("/{id}/reject")
    public Mono<ClaimResponseDTO> reject(@PathVariable Long id) {
        return claimService.reject(id);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public Mono<Void> deleteClaim(@PathVariable Long id) {
        return claimService.deleteClaim(id);
    }
}