package ec.edu.espe.backend.service;

import ec.edu.espe.backend.domain.LostItem;
import ec.edu.espe.backend.repository.LostItemRepository;
import ec.edu.espe.backend.service.impl.LostItemAuditService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class LostItemAuditServiceTest {

    @Mock
    private LostItemRepository lostItemRepository;

    private LostItemAuditService service;

    @BeforeEach
    void setUp() {
        service = new LostItemAuditService(lostItemRepository);
    }

    @Test
    void shouldCompleteAuditInBatchesAndReturnSummary() {
        when(lostItemRepository.findByActiveTrueOrderByIdAsc()).thenReturn(Flux.just(
                activeFoundItem(1L),
                activeFoundItem(2L),
                activeFoundItem(3L)
        ));

        StepVerifier.create(service.ejecutarAuditoria())
                .expectNext("Auditoría completada. Total procesados: 3")
                .verifyComplete();
    }

    @Test
    void shouldReturnDefaultMessageWhenAuditFails() {
        when(lostItemRepository.findByActiveTrueOrderByIdAsc()).thenReturn(Flux.error(new RuntimeException("DB caída")));

        StepVerifier.create(service.ejecutarAuditoria())
                .expectNext("Auditoría no disponible. Se devolvió el resultado predeterminado.")
                .verifyComplete();
    }

    private LostItem activeFoundItem(Long id) {
        LostItem item = new LostItem();
        item.setId(id);
        item.setName("Item " + id);
        item.setCategory("General");
        item.setStatus("FOUND");
        item.setActive(true);
        return item;
    }
}
