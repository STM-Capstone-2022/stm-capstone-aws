package edu.uwb.stmcapstone2022.alexaiot.providers;

import edu.uwb.stmcapstone2022.alexaiot.DirectiveHandler;
import edu.uwb.stmcapstone2022.alexaiot.DirectiveHandlerProvider;
import edu.uwb.stmcapstone2022.alexaiot.DirectiveName;
import edu.uwb.stmcapstone2022.alexaiot.alexa.model.*;
import edu.uwb.stmcapstone2022.alexaiot.endoints.ThingClient;
import edu.uwb.stmcapstone2022.alexaiot.endoints.ThingFactory;
import lombok.extern.slf4j.Slf4j;
import lombok.var;

import java.time.ZonedDateTime;
import java.util.Map;

@Slf4j
public final class AlexaPowerControllerProvider implements DirectiveHandlerProvider {
    private enum PowerState {
        ON,
        OFF
    }

    private SkillResponse<Void> turnOn(Directive<Void> request) {
        String endpointId = request.getEndpoint().getEndpointId();
        ThingClient device = ThingFactory.getInstance().get(endpointId);

        device.turnOn();
        return buildResponse(request, Property.<PowerState>builder()
                .namespace("Alexa.PowerController")
                .name("powerState")
                .value(PowerState.ON)
                .timeOfSample(ZonedDateTime.now())
                .uncertaintyInMilliseconds(50)
                .build());
    }

    private SkillResponse<Void> turnOff(Directive<Void> request) {
        String endpointId = request.getEndpoint().getEndpointId();
        ThingClient device = ThingFactory.getInstance().get(endpointId);

        device.turnOff();
        return buildResponse(request, Property.<PowerState>builder()
                .namespace("Alexa.PowerController")
                .name("powerState")
                .value(PowerState.OFF)
                .timeOfSample(ZonedDateTime.now())
                .uncertaintyInMilliseconds(50)
                .build());
    }

    private SkillResponse<Void> buildResponse(Directive<Void> request, Property<PowerState> powerState) {
        var endpointId = request.getEndpoint().getEndpointId();
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
                                .endpointId(endpointId)
                                .scope(Scope.builder()
                                        .type(Scope.Type.BEARER_TOKEN)
                                        .token(requestToken)
                                        .build())
                                .build())
                        .build())
                .build();
    }

    @Override
    public Map<DirectiveName, DirectiveHandler<?>> advertiseHandlers() {
        return Map.of(new DirectiveName("Alexa.PowerController", "TurnOn"),
                new DirectiveHandler<>(Void.class, this::turnOn),
                new DirectiveName("Alexa.PowerController", "TurnOff"),
                new DirectiveHandler<>(Void.class, this::turnOff));
    }
}
