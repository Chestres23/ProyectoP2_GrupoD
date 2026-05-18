package ec.edu.espe.backend.controller;

import ec.edu.espe.backend.dto.ClaimRequestDTO;
import ec.edu.espe.backend.dto.ClaimResponseDTO;
import ec.edu.espe.backend.service.ClaimService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/claims")
public class ClaimController {

    private final ClaimService claimService;

    public ClaimController(ClaimService claimService) {
        this.claimService = claimService;
    }

    @PostMapping
    public ResponseEntity<ClaimResponseDTO> create(@Valid @RequestBody ClaimRequestDTO request) {
        return ResponseEntity.status(201).body(claimService.createClaim(request));
    }

    @GetMapping
    public ResponseEntity<List<ClaimResponseDTO>> getAll() {
        return ResponseEntity.ok(claimService.findAll());
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping("/{id}/approve")
    public ResponseEntity<ClaimResponseDTO> approve(@PathVariable Long id) {
        return ResponseEntity.ok(claimService.approve(id));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping("/{id}/reject")
    public ResponseEntity<ClaimResponseDTO> reject(@PathVariable Long id) {
        return ResponseEntity.ok(claimService.reject(id));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteClaim(@PathVariable Long id) {
        claimService.deleteClaim(id);
        return ResponseEntity.noContent().build();
    }
}