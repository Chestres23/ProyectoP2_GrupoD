package ec.edu.espe.backend.service.impl;

import ec.edu.espe.backend.domain.LostItem;
import ec.edu.espe.backend.repository.LostItemRepository;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class LostItemAuditService {
    private static final String DEFAULT_ERROR_MESSAGE =
            "Auditoría no disponible. Se devolvió el resultado predeterminado.";
    private final LostItemRepository lostItemRepository;

    public LostItemAuditService(LostItemRepository lostItemRepository) {
        this.lostItemRepository = lostItemRepository;
    }

    public Mono<String> ejecutarAuditoria() {
        return Mono.create(sink -> {
            Flux<LostItem> flujoAuditoria = lostItemRepository.findByActiveTrueOrderByIdAsc()
                    .filter(item -> "FOUND".equals(item.getStatus()))
                    .handle((item, downstream) -> {
                        if (item.getCategory() == null || item.getCategory().isBlank()) {
                            System.out.println("[onErrorContinue] Item inválido, id=" + item.getId());
                            return;
                        }

                        downstream.next(item);
                    });

            flujoAuditoria.subscribe(new CustomSubscriber<>(
                    5,
                    totalProcesados -> sink.success("Auditoría completada. Total procesados: " + totalProcesados),
                    error -> sink.success(DEFAULT_ERROR_MESSAGE)
            ));
        });
    }
}
