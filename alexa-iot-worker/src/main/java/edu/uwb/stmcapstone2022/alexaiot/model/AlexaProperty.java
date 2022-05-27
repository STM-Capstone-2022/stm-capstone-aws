package edu.uwb.stmcapstone2022.alexaiot.model;

import lombok.Builder;
import lombok.Getter;

import java.time.ZonedDateTime;

@Builder
@Getter
public class AlexaProperty<T> {
    private final String namespace;
    private final String instance; // nullable
    private final String name;
    private final T value;
    private final ZonedDateTime timeOfSample;
    private final Integer uncertaintyInMilliseconds;
}
