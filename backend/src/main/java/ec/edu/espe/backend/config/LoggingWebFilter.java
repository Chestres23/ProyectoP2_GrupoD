package ec.edu.espe.backend.config;

import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

/**
 * WebFilter reactivo de logging — reemplaza el HandlerInterceptor de MVC.
 * Registra el método y URI de cada petición entrante.
 */
@Component
public class LoggingWebFilter implements WebFilter {

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        String method = exchange.getRequest().getMethod().name();
        String uri = exchange.getRequest().getPath().value();
        System.out.println("LOG FILTER -> Método: " + method + " | URI: " + uri);
        return chain.filter(exchange);
    }
}
