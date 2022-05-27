package edu.uwb.stmcapstone2022.alexaiot.model.request;

import edu.uwb.stmcapstone2022.alexaiot.model.AlexaScope;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class AlexaDiscoveryRequest {
    private final AlexaScope scope;
}
