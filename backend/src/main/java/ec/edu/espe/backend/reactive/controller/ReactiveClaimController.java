package ec.edu.espe.backend.reactive.controller;

import ec.edu.espe.backend.reactive.model.ReactiveClaimEvent;
import ec.edu.espe.backend.reactive.model.ReactiveStatsDTO;
import ec.edu.espe.backend.reactive.service.ReactiveClaimService;
import org.springframework.http.MediaType;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Controlador reactivo que expone los endpoints SSE y de estadísticas
 * para la demostración de la Práctica 3.
 *
 * Endpoints:
 * - GET /reactive/claims/stats → estadísticas asíncronas (Mono)
 * - GET /reactive/claims/stream → SSE hot stream en tiempo real (Flux)
 * - GET /reactive/claims/simulate → SSE con generación automática (Flux.interval)
 */
@RestController
@RequestMapping("/reactive/claims")
public class ReactiveClaimController {

    private final ReactiveClaimService reactiveClaimService;

    public ReactiveClaimController(ReactiveClaimService reactiveClaimService) {
        this.reactiveClaimService = reactiveClaimService;
    }

    /**
     * Estadísticas calculadas de forma asíncrona y no bloqueante.
     * Retorna un Mono que se resuelve cuando todas las queries terminan.
     */
    @GetMapping("/stats")
    public Mono<ReactiveStatsDTO> getStats() {
        return reactiveClaimService.computeStatsAsync();
    }

    /**
     * Stream SSE en tiempo real (hot stream).
     * Los eventos se emiten cuando se crean/aprueban/rechazan/eliminan reclamos
     * desde los endpoints regulares del sistema.
     * produces = TEXT_EVENT_STREAM_VALUE indica al navegador que es un stream SSE.
     */
    @GetMapping(value = "/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<ServerSentEvent<ReactiveClaimEvent>> streamEvents() {
        return reactiveClaimService.getEventStream()
                .map(event -> ServerSentEvent.<ReactiveClaimEvent>builder()
                        .id(event.getEventId())
                        .event("message")
                        .data(event)
                        .build());
    }

    /**
     * Simulación de actividad automática con Flux.interval.
     * Genera eventos cada 3 segundos para demostración visual.
     */
    @GetMapping(value = "/simulate", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<ServerSentEvent<ReactiveClaimEvent>> simulateActivity() {
        return reactiveClaimService.simulateClaimActivity()
                .map(event -> ServerSentEvent.<ReactiveClaimEvent>builder()
                        .id(event.getEventId())
                        .event("simulated-event")
                        .data(event)
                        .build());
    }
}
