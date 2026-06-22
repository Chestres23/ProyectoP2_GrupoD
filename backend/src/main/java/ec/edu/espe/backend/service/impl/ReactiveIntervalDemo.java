package ec.edu.espe.backend.service.impl;

import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.time.Duration;

@Service
public class ReactiveIntervalDemo {

    public void ejecutarDemoInterval() {
        Flux<Long> flujo = Flux.interval(Duration.ofMillis(500))
                .take(5);

        flujo.subscribe(new CustomSubscriber<>(2));
    }
}