package ec.edu.espe.backend.repository;

import ec.edu.espe.backend.domain.Claim;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Repositorio reactivo de reclamos.
 * Las queries ORDER BY y COUNT se definen con @Query ya que
 * R2DBC no soporta derived queries complejas como JPA.
 */
@Repository
public interface ClaimRepository extends ReactiveCrudRepository<Claim, Long> {

    Mono<Boolean> existsByUserIdAndItemId(Long userId, Long itemId);

    @Query("SELECT * FROM claims WHERE active = true ORDER BY claim_date DESC")
    Flux<Claim> findAllByActiveTrueOrderByClaimDateDesc();

    @Query("SELECT COUNT(*) FROM claims WHERE active = true AND status = :status")
    Mono<Long> countByActiveTrueAndStatus(String status);

    @Query("SELECT COUNT(*) FROM claims WHERE active = true")
    Mono<Long> countByActiveTrue();
}