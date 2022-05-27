package edu.uwb.stmcapstone2022.alexaiot.model;

import lombok.Builder;
import lombok.Getter;
import lombok.Singular;

import java.util.List;

@Builder
@Getter
public class AlexaContext {
    @Singular
    private final List<AlexaProperty<?>> properties;
}
