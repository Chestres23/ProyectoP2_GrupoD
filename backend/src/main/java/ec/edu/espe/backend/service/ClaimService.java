package ec.edu.espe.backend.service;

import ec.edu.espe.backend.dto.ClaimRequestDTO;
import ec.edu.espe.backend.dto.ClaimResponseDTO;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Servicio reactivo de reclamos — operaciones no bloqueantes con Mono/Flux.
 */
public interface ClaimService {
    Mono<ClaimResponseDTO> createClaim(ClaimRequestDTO request);
    Flux<ClaimResponseDTO> findAll();
    Mono<ClaimResponseDTO> approve(Long id);
    Mono<ClaimResponseDTO> reject(Long id);
    Mono<Void> deleteClaim(Long id);
}