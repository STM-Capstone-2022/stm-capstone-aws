package edu.uwb.stmcapstone2022.alexaiot.providers;

import edu.uwb.stmcapstone2022.alexaiot.DirectiveHandler;
import edu.uwb.stmcapstone2022.alexaiot.DirectiveHandlerProvider;
import edu.uwb.stmcapstone2022.alexaiot.DirectiveName;
import edu.uwb.stmcapstone2022.alexaiot.alexa.errors.InvalidDirectiveException;
import edu.uwb.stmcapstone2022.alexaiot.alexa.model.*;
import lombok.extern.slf4j.Slf4j;
import lombok.var;
import software.amazon.awssdk.core.SdkBytes;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.iotdataplane.IotDataPlaneClient;
import software.amazon.awssdk.services.iotdataplane.model.UpdateThingShadowRequest;
import software.amazon.awssdk.services.iotdataplane.model.UpdateThingShadowResponse;

import java.time.ZonedDateTime;
import java.util.Map;

@Slf4j
public final class AlexaPowerControllerProvider implements DirectiveHandlerProvider {
    private final String THING_NAME = System.getenv("THING_NAME");
    private final Region THING_REGION = Region.of(System.getenv("THING_REGION"));
    private final IotDataPlaneClient iotDataClient = IotDataPlaneClient
            .builder()
            .region(THING_REGION)
            .build();
    private final SdkBytes TURNON_PAYLOAD = SdkBytes.fromUtf8String(
            "{\"state\": {\"desired\": {\"LEDOn\": \"1\", \"powerOn\": \"1\"}}}");
    private final SdkBytes TURNOFF_PAYLOAD = SdkBytes.fromUtf8String(
            "{\"state\": {\"desired\": {\"LEDOn\": \"0\", \"powerOn\": \"0\"}}}");

    private enum PowerState {
        ON,
        OFF
    }

    private SkillResponse<Void> turnOn(Directive<Void> request) {
        String endpointId = request.getEndpoint().getEndpointId();
        if (!endpointId.equals("demo-device-01")) {
            throw new InvalidDirectiveException(DirectiveName.fromDirective(request),
                    "Device '" + endpointId + "' doesn't support directive");
        }

        UpdateThingShadowRequest thingRequest = UpdateThingShadowRequest.builder()
                .thingName(THING_NAME)
                .payload(TURNON_PAYLOAD)
                .build();

        log.info("Requesting thing [name={},region={}]", THING_NAME, THING_REGION);
        UpdateThingShadowResponse response = iotDataClient.updateThingShadow(thingRequest);
        log.info("Response metadata: {}", response.payload().asUtf8String());

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
        if (!endpointId.equals("demo-device-01")) {
            throw new InvalidDirectiveException(DirectiveName.fromDirective(request),
                    "Device '" + endpointId + "' doesn't support directive");
        }

        UpdateThingShadowRequest thingRequest = UpdateThingShadowRequest.builder()
                .thingName(THING_NAME)
                .payload(TURNOFF_PAYLOAD)
                .build();
        UpdateThingShadowResponse response = iotDataClient.updateThingShadow(thingRequest);
        log.info("Response metadata: {}", response.payload().asUtf8String());

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
