package edu.uwb.stmcapstone2022.alexaiot.alexa.model;

import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;

import java.util.Map;

@Builder(toBuilder = true)
@Getter
public class Endpoint {
    @NonNull private final String endpointId;
    private final Scope scope;
    private final Map<String, String> cookies;
}
