package edu.uwb.stmcapstone2022.alexaiot.model;

import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;

@Builder
@Getter
public class AlexaEvent<T> {
    @NonNull private final AlexaHeader header;
    private final AlexaEndpoint endpoint;
    @NonNull private final T payload;
}
