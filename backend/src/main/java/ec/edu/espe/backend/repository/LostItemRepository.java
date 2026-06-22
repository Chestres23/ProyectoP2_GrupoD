package ec.edu.espe.backend.repository;

import ec.edu.espe.backend.domain.LostItem;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Repositorio reactivo de objetos perdidos.
 */
@Repository
public interface LostItemRepository extends ReactiveCrudRepository<LostItem, Long> {

    Flux<LostItem> findByActiveTrue();

    Flux<LostItem> findByActiveTrueOrderByIdAsc();

    Mono<LostItem> findByIdAndActiveTrue(Long id);

    @Query("SELECT COUNT(*) FROM lost_items WHERE active = true AND status = :status")
    Mono<Long> countByActiveTrueAndStatus(String status);
}
