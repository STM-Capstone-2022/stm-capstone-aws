package edu.uwb.stmcapstone2022.alexaiot.alexa.model;

import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;

import java.time.ZonedDateTime;

@Builder
@Getter
public class Property<T> {
    @NonNull private final String namespace;
    private final String instance;
    @NonNull private final String name;
    @NonNull private final T value;
    @NonNull private final ZonedDateTime timeOfSample;
    @NonNull private final Integer uncertaintyInMilliseconds;
}
