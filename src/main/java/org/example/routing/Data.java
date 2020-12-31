package org.example.routing;

import io.rsocket.Payload;
import io.rsocket.util.DefaultPayload;
import lombok.Value;

import java.nio.charset.StandardCharsets;

@Value
public class Data {
    String metadata;
    String payload;

    public static Payload toRSocket(Data data) {
        return DefaultPayload.create(
                data.payload.getBytes(StandardCharsets.UTF_16),
                data.metadata.getBytes(StandardCharsets.UTF_16));
    }

    public static Data fromRSocket(Payload payload) {
        return new Data(new String(payload.getMetadata().array(), StandardCharsets.UTF_16),
                new String(payload.getData().array(), StandardCharsets.UTF_16));
    }
}
