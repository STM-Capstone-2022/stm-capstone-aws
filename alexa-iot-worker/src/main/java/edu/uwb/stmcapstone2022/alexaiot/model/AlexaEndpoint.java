package edu.uwb.stmcapstone2022.alexaiot.model;

import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;

import java.util.Map;

@Builder(builderClassName = "Builder")
@Getter
public class AlexaEndpoint {
    @NonNull private final String endpointId;
    private final AlexaScope scope;
    private final Map<String, String> cookie;
}
