package net.ilya.individualsapi.clients;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface GenericService<T, V> {
    Mono<T> findById(V v);

    Mono<T> create(T t);

    Mono<T> update(T t);

    Mono<T> deleted(V v);
    Flux<T> findAll();

}
