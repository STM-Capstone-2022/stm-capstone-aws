package edu.uwb.stmcapstone2022.alexaiot.alexa.model;

import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;

@Builder(toBuilder = true)
@Getter
public class Header {
    @NonNull private final String namespace;
    @NonNull private final String name;
    @NonNull private final String messageId;
    private final String correlationToken;
    @NonNull private final String payloadVersion;

    public Header.Builder toResponseBuilder() {
        return toBuilder().messageId(messageId + "-R");
    }
}
