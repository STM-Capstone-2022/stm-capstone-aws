package edu.uwb.stmcapstone2022.alexaiot.alexa.model;

import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;

@Builder
@Getter
public class Directive<T> {
    @NonNull private final Header header;
    private final Endpoint endpoint;
    private final T payload;
}
