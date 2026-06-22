package ec.edu.espe.backend.security;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

/**
 * Filtro JWT reactivo para WebFlux.
 * Reemplaza OncePerRequestFilter (Servlet) con WebFilter (reactivo).
 * Usa ReactiveSecurityContextHolder para propagar el contexto de seguridad.
 */
public class JwtWebFilter implements WebFilter {

    private final JwtService jwtService;
    private final ReactiveUserDetailsService userDetailsService;

    public JwtWebFilter(JwtService jwtService, ReactiveUserDetailsService userDetailsService) {
        this.jwtService = jwtService;
        this.userDetailsService = userDetailsService;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        String path = request.getPath().value();

        if (HttpMethod.OPTIONS.equals(request.getMethod())
                || path.startsWith("/api/auth/")
                || path.startsWith("/auth/")
                || path.startsWith("/api/reactive/")
                || path.startsWith("/reactive/")) {
            return chain.filter(exchange);
        }

        String authHeader = request.getHeaders().getFirst(HttpHeaders.AUTHORIZATION);

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return chain.filter(exchange);
        }

        String jwt = authHeader.substring(7);

        String email;
        try {
            email = jwtService.extractUsername(jwt);
        } catch (Exception ex) {
            return chain.filter(exchange);
        }

        if (email == null || email.isBlank()) {
            return chain.filter(exchange);
        }

        return userDetailsService.findByUsername(email)
                .filter(userDetails -> jwtService.isTokenValid(jwt, userDetails))
                .map(userDetails -> new UsernamePasswordAuthenticationToken(
                        userDetails,
                        null,
                        userDetails.getAuthorities()
                ))
                .flatMap(authToken ->
                        chain.filter(exchange)
                                .contextWrite(ReactiveSecurityContextHolder.withAuthentication(authToken))
                                .thenReturn(true)
                )
                .switchIfEmpty(Mono.defer(() ->
                        chain.filter(exchange)
                                .thenReturn(true)
                ))
                .then();
    }
}
