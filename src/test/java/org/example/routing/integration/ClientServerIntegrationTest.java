package org.example.routing.integration;

import org.example.routing.Data;
import org.example.routing.client.RSocketClient;
import org.example.routing.server.RSocketServer;
import org.example.routing.server.Router;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

public class ClientServerIntegrationTest {
    private static RSocketServer server;
    private static RSocketClient client;

    @BeforeAll
    public static void staticInit() {
        Router router = new Router();
        router.addMapping("route1", 1, data ->
                Flux.just(
                        new Data("metadata1", "data1"),
                        new Data("metadata2", "data2")
                ));

        server = RSocketServer.builder()
                .bindAddress("localhost")
                .bindPort(9000)
                .router(router)
                .build();

        server.runAsync();

        client = new RSocketClient("localhost", 9000);
        client.connect();
    }


    @Test
    public void test_client_reaches_server() {
        Flux<Data> stream = Flux.just(
                new Data("n/a", "1"),
                new Data("n/a", "2")
        );

        Flux<Data> result = client.requestChannel("route1", stream);
        result.as(StepVerifier::create)
                .expectNextCount(4);
    }
}
