package ec.edu.espe.backend.service.impl;
import ec.edu.espe.backend.domain.LostItem;
import ec.edu.espe.backend.repository.LostItemRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

@Service
public class LostItemAuditService {
    private final LostItemRepository lostItemRepository;

    public LostItemAuditService(LostItemRepository lostItemRepository) {
        this.lostItemRepository = lostItemRepository;
    }

    public Mono<String> ejecutarAuditoria() {
        return lostItemRepository.findByActiveTrue()
                .filter(item -> "FOUND".equals(item.getStatus()))
                .doOnSubscribe(subscription ->
                        System.out.println("[Subscriber] Suscripción iniciada."))
                .handle((item, sink) -> {
                    if (item.getCategory() == null || item.getCategory().isBlank()) {
                        System.out.println("[onErrorContinue] Item inválido, id=" + item.getId());
                        return;
                    }

                    System.out.println("[onNext] Procesado: " + item.getId());
                    sink.next(item);
                })
                .count()
                .map(totalProcesados -> {
                    System.out.println("[onComplete] Flujo finalizado.");
                    return "Auditoría completada. Total procesados: " + totalProcesados;
                });
    }
}