package org.example.routing.server;


import io.rsocket.Payload;
import org.example.routing.Data;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Flux;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

public class Router {
    private static final Logger logger = LoggerFactory.getLogger("router");

    private List<Route> routes = new ArrayList<>();

    public void addMapping(String path, int order, MappingFunction mapping) {
        routes.add(new Route(path, order, mapping));
    }

    public Flux<Payload> handle(Payload payload) {
        Data data = Data.fromRSocket(payload);
        logger.debug("Received data: {}", data);

        String path = data.getMetadata();
        Optional<Route> matched = routes.stream().filter(elem -> path.equals(elem.getPath()))
                .max(Comparator.comparingInt(Route::getOrder));

        if (matched.isPresent()) {
            logger.debug("Matched path {}", matched.get());
            return matched.get().getMapping().apply(data).map(Data::toRSocket);
        } else {
            logger.debug("No match found for {}", path);
            return Flux.empty();
        }
    }
}
