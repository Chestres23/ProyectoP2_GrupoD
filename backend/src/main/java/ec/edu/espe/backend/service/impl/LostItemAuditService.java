package ec.edu.espe.backend.service.impl;

import ec.edu.espe.backend.domain.LostItem;
import ec.edu.espe.backend.repository.LostItemRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class LostItemAuditService {

    @Autowired
    private LostItemRepository lostItemRepository;

    // Ejecuta auditoría reactiva con backpressure y retorna confirmación
    public Mono<String> ejecutarAuditoria() {
        Flux<LostItem> flujo = lostItemRepository.findByActiveTrue()
                .filter(item -> "FOUND".equals(item.getStatus()))
                .map(item -> {
                    if (item.getCategory() == null || item.getCategory().isBlank()) {
                        throw new IllegalStateException("Item inválido (sin categoría), id=" + item.getId());
                    }
                    return item;
                })
                .onErrorResume(error -> {
                    System.out.println("[onErrorResume] " + error.getMessage() + " — se omite y continúa.");
                    return Flux.empty();
                });

        flujo.subscribe(new CustomSubscriber<>(3));
        return Mono.just("Auditoría iniciada. Revisar logs del servidor.");
    }
}