package edu.uwb.stmcapstone2022.alexaiot.alexa.model.directive;

import edu.uwb.stmcapstone2022.alexaiot.alexa.model.Scope;
import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;

@Builder
@Getter
public class AlexaDiscoveryDiscover {
    @NonNull private final Scope scope;
}
