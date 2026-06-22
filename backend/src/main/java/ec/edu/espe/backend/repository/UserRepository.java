package ec.edu.espe.backend.repository;

import ec.edu.espe.backend.domain.User;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

/**
 * Repositorio reactivo de usuarios.
 * Extiende ReactiveCrudRepository → todos los métodos CRUD devuelven Mono/Flux.
 */
@Repository
public interface UserRepository extends ReactiveCrudRepository<User, Long> {

    // Busca un usuario por email exacto
    Mono<User> findByEmail(String email);

    // Verifica si existe un usuario con el email dado
    Mono<Boolean> existsByEmail(String email);
}