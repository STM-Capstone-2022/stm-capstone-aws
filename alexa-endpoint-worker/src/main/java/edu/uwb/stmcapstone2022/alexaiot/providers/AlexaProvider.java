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
public final class AlexaProvider implements DirectiveHandlerProvider {
    private enum PowerState {
        ON,
        OFF
    }

    private Context getThingContext(String endpointName) {
        ThingClient device = ThingFactory.getInstance().get(endpointName);
        boolean isOn = device.isPoweredOn();

        return Context.builder()
                .property(Property.<PowerState>builder()
                        .namespace("Alexa.PowerController")
                        .name("powerState")
                        .value(isOn ? PowerState.ON : PowerState.OFF)
                        .timeOfSample(ZonedDateTime.now())
                        .uncertaintyInMilliseconds(50)
                        .build())
                .property(Property.builder()
                        .namespace("Alexa.EndpointHealth")
                        .name("connectivity")
                        .value(Map.of("value", "OK"))
                        .timeOfSample(ZonedDateTime.now())
                        .uncertaintyInMilliseconds(50)
                        .build())
                .build();

    }

    private SkillResponse<Void> reportState(Directive<Void> request) {
        var endpointId = request.getEndpoint().getEndpointId();
        var requestToken = request.getEndpoint().getScope().getToken();

        return SkillResponse.<Void>builder()
                .context(getThingContext(endpointId))
                .event(Event.<Void>builder()
                        .header(request.getHeader().toResponseBuilder()
                                .namespace("Alexa")
                                .name("StateReport")
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
        return Map.of(new DirectiveName("Alexa", "ReportState"),
                new DirectiveHandler<>(Void.class, this::reportState));
    }
}
