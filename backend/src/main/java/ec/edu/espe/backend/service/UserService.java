package ec.edu.espe.backend.service;

import ec.edu.espe.backend.domain.User;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Servicio reactivo de usuarios — todos los métodos retornan Mono o Flux.
 */
public interface UserService {
    Mono<User> save(User user);
    Mono<User> findById(Long id);
    Flux<User> findAll();
    Mono<User> findByEmail(String email);
    Mono<Void> deactivate(Long id);
}