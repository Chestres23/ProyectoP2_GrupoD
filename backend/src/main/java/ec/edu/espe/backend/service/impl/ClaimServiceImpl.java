package ec.edu.espe.backend.service.impl;

import ec.edu.espe.backend.domain.Claim;
import ec.edu.espe.backend.dto.ClaimRequestDTO;
import ec.edu.espe.backend.dto.ClaimResponseDTO;
import ec.edu.espe.backend.exception.ClaimNotFoundException;
import ec.edu.espe.backend.exception.DuplicateClaimException;
import ec.edu.espe.backend.exception.InvalidClaimStateException;
import ec.edu.espe.backend.exception.ItemNotFoundException;
import ec.edu.espe.backend.exception.UserNotFoundException;
import ec.edu.espe.backend.repository.ClaimRepository;
import ec.edu.espe.backend.repository.LostItemRepository;
import ec.edu.espe.backend.repository.UserRepository;
import ec.edu.espe.backend.service.ClaimService;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

/**
 * Implementación reactiva del servicio de reclamos.
 * Cada operación es una cadena no bloqueante de Mono/Flux.
 * Las relaciones (User, LostItem) se resuelven con flatMap manual
 * ya que R2DBC no tiene lazy loading ni joins automáticos.
 */
@Service
public class ClaimServiceImpl implements ClaimService {

    private final ClaimRepository claimRepository;
    private final UserRepository userRepository;
    private final LostItemRepository lostItemRepository;

    // Inyección opcional del servicio reactivo SSE (puede ser null en tests básicos)
    private final ec.edu.espe.backend.reactive.service.ReactiveClaimService reactiveClaimService;

    public ClaimServiceImpl(ClaimRepository claimRepository,
                            UserRepository userRepository,
                            LostItemRepository lostItemRepository,
                            org.springframework.beans.factory.ObjectProvider<ec.edu.espe.backend.reactive.service.ReactiveClaimService> reactiveProvider) {
        this.claimRepository = claimRepository;
        this.userRepository = userRepository;
        this.lostItemRepository = lostItemRepository;
        this.reactiveClaimService = reactiveProvider.getIfAvailable();
    }

    @Override
    public Mono<ClaimResponseDTO> createClaim(ClaimRequestDTO request) {
        return claimRepository.existsByUserIdAndItemId(request.getUserId(), request.getItemId())
                .flatMap(exists -> {
                    if (exists) {
                        return Mono.error(new DuplicateClaimException("El usuario ya registró un reclamo para este objeto."));
                    }
                    return userRepository.findById(request.getUserId())
                            .switchIfEmpty(Mono.error(new UserNotFoundException("Usuario no encontrado.")));
                })
                .flatMap(user ->
                    lostItemRepository.findByIdAndActiveTrue(request.getItemId())
                            .switchIfEmpty(Mono.error(new ItemNotFoundException("Objeto no encontrado o inactivo.")))
                            .flatMap(item -> {
                                if (!"FOUND".equals(item.getStatus())) {
                                    return Mono.error(new InvalidClaimStateException("Solo se puede reclamar un objeto en estado FOUND."));
                                }
                                Claim claim = new Claim();
                                claim.setUserId(user.getId());
                                claim.setItemId(item.getId());
                                claim.setObservation(request.getObservation().trim());
                                claim.setStatus("PENDING");
                                LocalDateTime now = LocalDateTime.now();
                                claim.setClaimDate(now);
                                claim.setCreatedAt(now);
                                claim.setUpdatedAt(now);
                                return claimRepository.save(claim)
                                        .map(saved -> {
                                            // Emitir evento al hot stream SSE
                                            emitEvent("CREATED", saved, item.getName(), user.getName());
                                            return buildDTO(saved, user.getId(), user.getName(), user.getEmail(),
                                                    item.getId(), item.getName(), item.getStatus(),
                                                    item.getCategory(), item.getLocationFound());
                                        });
                            })
                );
    }

    @Override
    public Flux<ClaimResponseDTO> findAll() {
        return claimRepository.findAllByActiveTrueOrderByClaimDateDesc()
                .flatMap(this::enrichClaimDTO);
    }

    @Override
    public Mono<ClaimResponseDTO> approve(Long id) {
        return claimRepository.findById(id)
                .switchIfEmpty(Mono.error(new ClaimNotFoundException("Reclamo no encontrado.")))
                .flatMap(claim -> {
                    if (!"PENDING".equals(claim.getStatus())) {
                        return Mono.error(new InvalidClaimStateException("Solo se puede aprobar un reclamo pendiente."));
                    }
                    return lostItemRepository.findById(claim.getItemId())
                            .flatMap(item -> {
                                if (!"FOUND".equals(item.getStatus())) {
                                    return Mono.error(new InvalidClaimStateException("El objeto ya fue reclamado o entregado."));
                                }
                                claim.setStatus("APPROVED");
                                claim.setUpdatedAt(LocalDateTime.now());
                                item.setStatus("CLAIMED");
                                item.setUpdatedAt(LocalDateTime.now());
                                return lostItemRepository.save(item)
                                        .then(claimRepository.save(claim))
                                        .flatMap(saved -> enrichClaimDTO(saved)
                                                .doOnNext(dto -> emitEvent("APPROVED", saved, item.getName(), null)));
                            });
                });
    }

    @Override
    public Mono<ClaimResponseDTO> reject(Long id) {
        return claimRepository.findById(id)
                .switchIfEmpty(Mono.error(new ClaimNotFoundException("Reclamo no encontrado.")))
                .flatMap(claim -> {
                    if (!"PENDING".equals(claim.getStatus())) {
                        return Mono.error(new InvalidClaimStateException("Solo se puede rechazar un reclamo pendiente."));
                    }
                    claim.setStatus("REJECTED");
                    claim.setUpdatedAt(LocalDateTime.now());
                    return claimRepository.save(claim)
                            .flatMap(saved -> enrichClaimDTO(saved)
                                    .doOnNext(dto -> emitEvent("REJECTED", saved, dto.getItemName(), null)));
                });
    }

    @Override
    public Mono<Void> deleteClaim(Long id) {
        return claimRepository.findById(id)
                .switchIfEmpty(Mono.error(new ClaimNotFoundException("Reclamo no encontrado.")))
                .flatMap(claim -> {
                    claim.setActive(false);
                    claim.setUpdatedAt(LocalDateTime.now());
                    return claimRepository.save(claim)
                            .doOnNext(saved -> emitEvent("DELETED", saved, null, null));
                })
                .then();
    }

    /**
     * Enriquece un Claim con datos de User y LostItem para armar el DTO completo.
     * En R2DBC no hay lazy loading, se hace manualmente.
     */
    private Mono<ClaimResponseDTO> enrichClaimDTO(Claim claim) {
        Mono<ec.edu.espe.backend.domain.User> userMono = userRepository.findById(claim.getUserId())
                .defaultIfEmpty(new ec.edu.espe.backend.domain.User());
        Mono<ec.edu.espe.backend.domain.LostItem> itemMono = lostItemRepository.findById(claim.getItemId())
                .defaultIfEmpty(new ec.edu.espe.backend.domain.LostItem());
        return Mono.zip(userMono, itemMono)
                .map(tuple -> buildDTO(claim,
                        tuple.getT1().getId(), tuple.getT1().getName(), tuple.getT1().getEmail(),
                        tuple.getT2().getId(), tuple.getT2().getName(), tuple.getT2().getStatus(),
                        tuple.getT2().getCategory(), tuple.getT2().getLocationFound()));
    }

    private ClaimResponseDTO buildDTO(Claim claim, Long userId, String userName, String userEmail,
                                       Long itemId, String itemName, String itemStatus,
                                       String itemCategory, String itemLocation) {
        ClaimResponseDTO dto = new ClaimResponseDTO();
        dto.setId(claim.getId());
        dto.setClaimDate(claim.getClaimDate());
        dto.setObservation(claim.getObservation());
        dto.setStatus(claim.getStatus());
        dto.setUserId(userId);
        dto.setUserName(userName);
        dto.setUserEmail(userEmail);
        dto.setItemId(itemId);
        dto.setItemName(itemName);
        dto.setItemStatus(itemStatus);
        dto.setItemCategory(itemCategory);
        dto.setItemLocation(itemLocation);
        return dto;
    }

    /**
     * Emite un evento al hot stream SSE si el servicio reactivo está disponible.
     */
    private void emitEvent(String type, Claim claim, String itemName, String userName) {
        if (reactiveClaimService != null) {
            reactiveClaimService.emitEvent(type, claim.getId(), itemName, userName, claim.getStatus());
        }
    }
}