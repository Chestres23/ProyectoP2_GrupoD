package ec.edu.espe.backend.service.impl;

import ec.edu.espe.backend.domain.LostItem;
import ec.edu.espe.backend.domain.enums.ItemStatus;
import ec.edu.espe.backend.repository.LostItemRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.time.Duration;
import java.util.List;

@Service
public class LostItemAuditService {

    @Autowired
    private LostItemRepository lostItemRepository;

    public void ejecutarAuditoria() {
        List<LostItem> items = lostItemRepository.findByStatus(ItemStatus.FOUND);

        Flux<LostItem> flujo = Flux.fromIterable(items)
                .delayElements(Duration.ofMillis(500))
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
    }
}