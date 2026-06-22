package ec.edu.espe.backend.reactive.service;

import ec.edu.espe.backend.reactive.model.ClaimEventType;
import ec.edu.espe.backend.reactive.model.ReactiveClaimEvent;
import ec.edu.espe.backend.reactive.model.ReactiveStatsDTO;
import ec.edu.espe.backend.repository.ClaimRepository;
import ec.edu.espe.backend.repository.LostItemRepository;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Sinks;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Servicio reactivo para la extensión de la Práctica 3 (WebFlux).
 *
 * Conceptos demostrados:
 * - Sinks.Many: hot stream (publicador caliente) para emitir eventos SSE en tiempo real.
 * - Mono: cálculo asíncrono no bloqueante de estadísticas.
 * - Flux.interval: generación automática periódica de eventos simulados.
 * - SSE (Server-Sent Events): stream en vivo consumible desde el frontend.
 */
@Service
public class ReactiveClaimService {

    private final ClaimRepository claimRepository;
    private final LostItemRepository lostItemRepository;

    /**
     * Sinks.Many actúa como "publicador caliente" (hot publisher).
     * Los eventos emitidos aquí son enviados a todos los suscriptores SSE en tiempo real.
     * multicast() permite múltiples suscriptores simultáneos.
     * onBackpressureBuffer() maneja la contrapresión almacenando eventos temporalmente.
     */
    private final Sinks.Many<ReactiveClaimEvent> eventSink =
            Sinks.many().multicast().onBackpressureBuffer();

    public ReactiveClaimService(ClaimRepository claimRepository,
                                 LostItemRepository lostItemRepository) {
        this.claimRepository = claimRepository;
        this.lostItemRepository = lostItemRepository;
    }

    /**
     * Emite un evento al hot stream. Invocado desde ClaimServiceImpl
     * cuando se crea, aprueba, rechaza o elimina un reclamo.
     */
    public void emitEvent(String type, Long claimId, String itemName, String userName, String status) {
        ReactiveClaimEvent event = new ReactiveClaimEvent(
                UUID.randomUUID().toString(),
                ClaimEventType.valueOf(type),
                claimId,
                itemName != null ? itemName : "N/A",
                userName != null ? userName : "N/A",
                status
        );
        eventSink.tryEmitNext(event);
    }

    /**
     * Retorna el Flux del hot stream para consumo SSE.
     * Cada suscriptor (cliente SSE) recibe los eventos en tiempo real.
     */
    public Flux<ReactiveClaimEvent> getEventStream() {
        return eventSink.asFlux();
    }

    /**
     * Cálculo asíncrono no bloqueante de estadísticas.
     * Demuestra el uso de Mono.zip para combinar múltiples queries reactivas
     * en un solo resultado — equivalente al "promedio asíncrono" del laboratorio
     * pero aplicado al dominio del proyecto.
     */
    public Mono<ReactiveStatsDTO> computeStatsAsync() {
        return Mono.zip(
                lostItemRepository.count(),
                claimRepository.countByActiveTrue(),
                claimRepository.countByActiveTrueAndStatus("PENDING"),
                claimRepository.countByActiveTrueAndStatus("APPROVED"),
                claimRepository.countByActiveTrueAndStatus("REJECTED"),
                lostItemRepository.countByActiveTrueAndStatus("DELIVERED")
        ).map(tuple -> {
            ReactiveStatsDTO stats = new ReactiveStatsDTO();
            stats.setTotalItems(tuple.getT1());
            stats.setTotalClaims(tuple.getT2());
            stats.setPendingClaims(tuple.getT3());
            stats.setApprovedClaims(tuple.getT4());
            stats.setRejectedClaims(tuple.getT5());
            stats.setDeliveredItems(tuple.getT6());
            // Tasa de aprobación: aprobados / total de reclamos procesados
            long processed = tuple.getT4() + tuple.getT5();
            stats.setApprovalRate(processed > 0 ? (double) tuple.getT4() / processed * 100 : 0.0);
            stats.setTimestamp(LocalDateTime.now());
            return stats;
        });
    }

    /**
     * Simula actividad de reclamos generando eventos cada 3 segundos.
     * Demuestra Flux.interval para generación automática periódica — equivalente
     * a la simulación de sensores del laboratorio, adaptada al dominio del proyecto.
     */
    public Flux<ReactiveClaimEvent> simulateClaimActivity() {
        AtomicLong counter = new AtomicLong(0);
        String[] items = {"Laptop HP", "Mochila negra", "Celular Samsung", "Llaves", "Calculadora", "Audífonos"};
        String[] users = {"María García", "Carlos López", "Ana Martínez", "Pedro Rojas"};
        ClaimEventType[] types = ClaimEventType.values();

        return Flux.interval(Duration.ofSeconds(3))
                .map(tick -> {
                    long idx = counter.getAndIncrement();
                    String item = items[(int) (idx % items.length)];
                    String user = users[(int) (idx % users.length)];
                    ClaimEventType type = types[(int) (idx % types.length)];
                    return new ReactiveClaimEvent(
                            UUID.randomUUID().toString(),
                            type,
                            1000L + idx,
                            item,
                            user,
                            type == ClaimEventType.APPROVED ? "APPROVED" : type == ClaimEventType.REJECTED ? "REJECTED" : "PENDING"
                    );
                });
    }
}
