package edu.uwb.stmcapstone2022.alexaiot;

import edu.uwb.stmcapstone2022.alexaiot.alexa.model.*;
import lombok.var;

import java.time.ZonedDateTime;
import java.util.Map;

public class AlexaHandler {
    private enum PowerState {
        ON,
        OFF
    }

    public SkillResponse<Void> handleTurnOn(Directive<Void> request) {
        return buildResponse(request, Property.<PowerState>builder()
                .namespace("Alexa.PowerController")
                .name("powerState")
                .value(PowerState.ON)
                .timeOfSample(ZonedDateTime.now())
                .uncertaintyInMilliseconds(50)
                .build());
    }

    public SkillResponse<Void> handleTurnOff(Directive<Void> request) {
        return buildResponse(request, Property.<PowerState>builder()
                .namespace("Alexa.PowerController")
                .name("powerState")
                .value(PowerState.OFF)
                .timeOfSample(ZonedDateTime.now())
                .uncertaintyInMilliseconds(50)
                .build());
    }

    private SkillResponse<Void> buildResponse(Directive<Void> request, Property<PowerState> powerState) {
        var requestToken = request.getEndpoint().getScope().getToken();

        var context = Context.builder()
                .property(powerState)
                .property(Property.builder()
                        .namespace("Alexa.EndpointHealth")
                        .name("connectivity")
                        .value(Map.of("value", "OK"))
                        .timeOfSample(ZonedDateTime.now())
                        .uncertaintyInMilliseconds(50)
                        .build())
                .build();

        return SkillResponse.<Void>builder()
                .context(context)
                .event(Event.<Void>builder()
                        .header(request.getHeader().toResponseBuilder()
                                .namespace("Alexa")
                                .name("Response")
                                .build())
                        .endpoint(Endpoint.builder()
                                .scope(Scope.builder()
                                        .type(Scope.Type.BEARER_TOKEN)
                                        .token(requestToken)
                                        .build())
                                .endpointId("sample-bulb-01")
                                .build())
                        .build())
                .build();
    }
}
