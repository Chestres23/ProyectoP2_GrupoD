package ec.edu.espe.backend.security;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

/**
 * Filtro JWT reactivo para WebFlux.
 * Reemplaza OncePerRequestFilter (Servlet) con WebFilter (reactivo).
 * Usa ReactiveSecurityContextHolder para propagar el contexto de seguridad.
 */
@Component
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

        // Omitir filtro para OPTIONS y rutas públicas
        if (HttpMethod.OPTIONS.equals(request.getMethod())
                || path.startsWith("/api/auth/")
                || path.startsWith("/api/reactive/")) {
            return chain.filter(exchange);
        }

        String authHeader = request.getHeaders().getFirst(HttpHeaders.AUTHORIZATION);

        // Si no hay token, continuar sin autenticar
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return chain.filter(exchange);
        }

        String jwt = authHeader.substring(7);
        try {
            String email = jwtService.extractUsername(jwt);
            if (email == null) {
                return chain.filter(exchange);
            }

            // Cargar usuario y validar token de forma reactiva
            return userDetailsService.findByUsername(email)
                    .filter(userDetails -> jwtService.isTokenValid(jwt, userDetails))
                    .flatMap(userDetails -> {
                        UsernamePasswordAuthenticationToken authToken =
                                new UsernamePasswordAuthenticationToken(
                                        userDetails, null, userDetails.getAuthorities());
                        // Propagar el contexto de seguridad reactivamente
                        return chain.filter(exchange)
                                .contextWrite(ReactiveSecurityContextHolder
                                        .withAuthentication(authToken));
                    })
                    .switchIfEmpty(chain.filter(exchange));
        } catch (Exception e) {
            // Token inválido o expirado → continuar sin autenticar
            return chain.filter(exchange);
        }
    }
}
