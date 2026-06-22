package ec.edu.espe.backend.service.impl;

import ec.edu.espe.backend.repository.UserRepository;
import ec.edu.espe.backend.security.UserPrincipal;
import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

/**
 * Implementación reactiva de UserDetailsService.
 * WebFlux requiere ReactiveUserDetailsService en vez del bloqueante UserDetailsService.
 */
@Service
public class UserDetailsServiceImpl implements ReactiveUserDetailsService {

    private final UserRepository userRepository;

    public UserDetailsServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public Mono<UserDetails> findByUsername(String email) {
        return userRepository.findByEmail(email)
                .map(user -> (UserDetails) new UserPrincipal(user))
                .switchIfEmpty(Mono.error(
                        new UsernameNotFoundException("Usuario no encontrado: " + email)));
    }
}
