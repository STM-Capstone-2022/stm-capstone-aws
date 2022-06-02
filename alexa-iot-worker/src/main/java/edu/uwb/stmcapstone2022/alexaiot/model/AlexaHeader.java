package edu.uwb.stmcapstone2022.alexaiot.model;

import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;

@Builder(builderClassName = "Builder", toBuilder = true)
@Getter
public class AlexaHeader {
    @NonNull private final String namespace;
    @NonNull private final String name;
    @NonNull private final String messageId;
    private final String correlationToken;
    @NonNull private final String payloadVersion;

    public AlexaHeader.Builder toResponseBuilder() {
        return toBuilder().messageId(messageId + "-R");
    }
}
