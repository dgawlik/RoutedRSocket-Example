package org.example.routing.server;

import org.example.routing.Data;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

public class RSocketServerTest {
    private static RSocketServer sut;

    @BeforeAll
    public static void staticInit() {
        Router router = new Router();
        router.addMapping("hola!🚗", 1, buf -> Flux.just(
                buf,
                new Data(buf.getMetadata(), "hello 🚗")
        ));
        router.addMapping("ola 🥇", 1, buf -> Flux.just(
                new Data(buf.getMetadata(), "2 🥇")
        ));

        router.addMapping("ola 🥇", 2, buf -> Flux.just(
                new Data(buf.getMetadata(), "3 🥇")
        ));

        sut = RSocketServer.builder()
                .bindAddress("localhost")
                .bindPort(9000)
                .router(router)
                .build();
    }

    @Test
    @DisplayName("Test if matched router function returns Flux 😮")
    public void test_matches_route() {
        Data p = new Data("hola!🚗", "1");

        Flux<io.rsocket.Payload> result = sut.router(Flux.just(Data.toRSocket(p)));

        result.map(Data::fromRSocket)
                .as(StepVerifier::create)
                .expectNext(new Data("hola!🚗", "1"),
                        new Data("hola!🚗", "hello 🚗"))
                .expectComplete();
    }

    @Test
    @DisplayName("Test if matches function with higher predecence 😮")
    public void test_matches_collision() {
        Data p = new Data("ola 🥇", "1");

        Flux<io.rsocket.Payload> result = sut.router(Flux.just(Data.toRSocket(p)));

        result.map(Data::fromRSocket)
                .as(StepVerifier::create)
                .expectNext(new Data("ola 🥇", "3 🥇"))
                .expectComplete();
    }

    @Test
    @DisplayName("On no match returs empty flux 😮")
    public void test_no_match() {
        Data p = new Data("something.🐱", "1");

        Flux<io.rsocket.Payload> result = sut.router(Flux.just(Data.toRSocket(p)));

        result.map(Data::fromRSocket)
                .as(StepVerifier::create)
                .expectComplete();
    }

    @Test
    @DisplayName("Acts as a flatMap 😮")
    public void test_multiple_messages() {
        Data p = new Data("hola!🚗", "1");
        Data p2 = new Data("ola 🥇", "1");


        Flux<io.rsocket.Payload> result = sut.router(
                Flux.just(p, p2).map(Data::toRSocket));

        result.map(Data::fromRSocket)
                .as(StepVerifier::create)
                .expectNextCount(3)
                .expectComplete();
    }
}
