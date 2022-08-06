package edu.uwb.stmcapstone2022.alexaiot.alexa.model;

import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;

@Builder
@Getter
public class Event<T> {
    @NonNull private final Header header;
    private final T payload;
    private final Endpoint endpoint;
}
