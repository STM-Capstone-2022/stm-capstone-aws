package edu.uwb.stmcapstone2022.alexaiot.model;

import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;

@Builder
@Getter
public class AlexaScope {
    @NonNull private final String type;
    @NonNull private final String token;
}
