package edu.uwb.stmcapstone2022.alexaiot.model;

import edu.uwb.stmcapstone2022.alexaiot.AlexaHandler;
import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;

@Builder(toBuilder = true)
@Getter
public class AlexaHeader {
    @NonNull private final String namespace;
    @NonNull private final String name;
    @NonNull private final String messageId;
    private final String correlationToken;
    @NonNull private final String payloadVersion;

    public AlexaHeader toResponseHeader() {
        return toBuilder()
                .namespace("Alexa")
                .name("Response")
                .messageId(messageId + "-R")
                .build();
    }
}
