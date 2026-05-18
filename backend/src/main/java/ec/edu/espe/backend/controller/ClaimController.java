package ec.edu.espe.backend.controller;

import ec.edu.espe.backend.dto.ClaimRequestDTO;
import ec.edu.espe.backend.dto.ClaimResponseDTO;
import ec.edu.espe.backend.service.ClaimService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/claims")
public class ClaimController {

    private final ClaimService claimService;

    public ClaimController(ClaimService claimService) {
        this.claimService = claimService;
    }

    @PostMapping
    public ResponseEntity<ClaimResponseDTO> create(@RequestBody @Valid ClaimRequestDTO dto, Principal principal){
        // For now we don't have JWT mapping to user id; accept a mock user id (in real app extract from token)
        Long userId = 1L; // TODO: replace with actual authenticated user id
        ClaimResponseDTO created = claimService.create(dto, userId);
        return ResponseEntity.created(URI.create("/claims/" + created.getId())).body(created);
    }

    @GetMapping
    public ResponseEntity<List<ClaimResponseDTO>> list(){
        return ResponseEntity.ok(claimService.findAll());
    }

    @PatchMapping("/{id}/approve")
    public ResponseEntity<Void> approve(@PathVariable Long id){
        claimService.approve(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/reject")
    public ResponseEntity<Void> reject(@PathVariable Long id){
        claimService.reject(id);
        return ResponseEntity.noContent().build();
    }
}
