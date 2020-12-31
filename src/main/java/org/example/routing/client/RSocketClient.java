package org.example.routing.client;

import io.rsocket.Payload;
import io.rsocket.RSocket;
import io.rsocket.core.RSocketConnector;
import io.rsocket.transport.netty.client.TcpClientTransport;
import io.rsocket.util.DefaultPayload;
import lombok.AllArgsConstructor;
import lombok.Builder;
import org.example.routing.Data;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Flux;

public class RSocketClient {
    private final String address;
    private final int port;
    private RSocket socket;

    private static final Logger logger = LoggerFactory.getLogger("client");

    public RSocketClient(String address, int port) {
        this.address = address;
        this.port = port;
    }

    public void connect() {
        socket =
                RSocketConnector.connectWith(
                        TcpClientTransport.create(this.address, this.port)).block();
    }

    public Flux<Data> requestChannel(String path, Flux<Data> in) {
        //TODO more refined way to handle routing based on mutual contract
        // not using whole metadata
        Flux<Payload> flux = in.map(x -> new Data(path, x.getPayload()))
                .map(Data::toRSocket);

        return socket.requestChannel(flux)
                .map(Data::fromRSocket)
                .doOnNext(d -> logger.debug("Client received {}", d))
                .doFinally(signalType -> socket.dispose());
    }
}
