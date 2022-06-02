package edu.uwb.stmcapstone2022.alexaiot.model;

import lombok.Builder;
import lombok.Getter;

@Builder(builderClassName = "Builder")
@Getter
public class SmartHomeRequest<T> {
    private final AlexaHeader header;
    private final AlexaEndpoint endpoint;
    private final T payload;

    public static <T> SmartHomeRequest<T> fromDirective(AlexaDirective directive, T payload) {
        return SmartHomeRequest.<T>builder()
                .header(directive.getHeader())
                .endpoint(directive.getEndpoint())
                .payload(payload)
                .build();
    }
}
