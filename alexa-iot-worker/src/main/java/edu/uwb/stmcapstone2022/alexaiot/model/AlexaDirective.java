package edu.uwb.stmcapstone2022.alexaiot.model;

import com.google.gson.JsonObject;
import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;

@Builder(builderClassName = "Builder")
@Getter
public class AlexaDirective {
    @NonNull private final AlexaHeader header;
    private final AlexaEndpoint endpoint;
    private final JsonObject payload;
}
