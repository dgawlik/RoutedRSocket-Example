package org.example.routing.server;

import org.example.routing.Data;
import reactor.core.publisher.Flux;

import java.util.function.Function;

@FunctionalInterface
public interface MappingFunction extends Function<Data, Flux<Data>> {
}
