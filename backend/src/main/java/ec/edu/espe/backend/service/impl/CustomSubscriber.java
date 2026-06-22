package ec.edu.espe.backend.service.impl;

import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

import java.util.function.Consumer;

public class CustomSubscriber<T> implements Subscriber<T> {

    private Subscription subscription;
    private int count = 0;
    private final int batchSize;
    private final Consumer<Integer> onCompleteCallback;
    private final Consumer<Throwable> onErrorCallback;

    public CustomSubscriber(int batchSize) {
        this(batchSize, null, null);
    }

    public CustomSubscriber(int batchSize,
                            Consumer<Integer> onCompleteCallback,
                            Consumer<Throwable> onErrorCallback) {
        this.batchSize = batchSize;
        this.onCompleteCallback = onCompleteCallback;
        this.onErrorCallback = onErrorCallback;
    }

    @Override
    public void onSubscribe(Subscription s) {
        this.subscription = s;
        System.out.println("[Subscriber] Suscripción iniciada. Solicitando " + batchSize + " elementos.");
        subscription.request(batchSize);
    }

    @Override
    public void onNext(T value) {
        count++;
        System.out.println("[onNext] Procesado: " + value);
        if (count % batchSize == 0) {
            System.out.println("[Subscriber] Solicitando " + batchSize + " elementos más.");
            subscription.request(batchSize);
        }
    }

    @Override
    public void onError(Throwable t) {
        System.out.println("[onError] " + t.getMessage());
        if (onErrorCallback != null) {
            onErrorCallback.accept(t);
        }
    }

    @Override
    public void onComplete() {
        System.out.println("[onComplete] Flujo finalizado. Total procesados: " + count);
        if (onCompleteCallback != null) {
            onCompleteCallback.accept(count);
        }
    }
}
