package org.example.routing.server;

import io.rsocket.Payload;
import io.rsocket.SocketAcceptor;
import io.rsocket.transport.netty.server.CloseableChannel;
import io.rsocket.transport.netty.server.TcpServerTransport;
import lombok.Builder;
import org.reactivestreams.Publisher;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Builder
public class RSocketServer {
    String bindAddress;
    Integer bindPort;
    Router router;

    public void run() {
        runAsync()
            .onClose()
            .block();
    }

    public CloseableChannel runAsync() {
        SocketAcceptor acceptor = SocketAcceptor.forRequestChannel(this::router);

        return io.rsocket.core.RSocketServer.create(acceptor)
                .bind(TcpServerTransport.create(bindAddress, bindPort))
                .block();
    }


    Flux<Payload> router(Publisher<Payload> incoming) {
        return Flux.from(incoming).switchMap(router::handle);
    }
}
