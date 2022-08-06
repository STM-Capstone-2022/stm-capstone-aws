package edu.uwb.stmcapstone2022.alexaiot.providers;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import edu.uwb.stmcapstone2022.alexaiot.DirectiveHandler;
import edu.uwb.stmcapstone2022.alexaiot.DirectiveHandlerProvider;
import edu.uwb.stmcapstone2022.alexaiot.DirectiveName;
import edu.uwb.stmcapstone2022.alexaiot.alexa.errors.InvalidDirectiveException;
import edu.uwb.stmcapstone2022.alexaiot.alexa.model.*;
import lombok.extern.slf4j.Slf4j;
import lombok.var;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.iotdataplane.IotDataPlaneClient;
import software.amazon.awssdk.services.iotdataplane.model.GetThingShadowRequest;
import software.amazon.awssdk.services.iotdataplane.model.GetThingShadowResponse;

import java.io.InputStreamReader;
import java.time.ZonedDateTime;
import java.util.Map;

@Slf4j
public final class AlexaProvider implements DirectiveHandlerProvider {
    private final String SENSOR_NAME = System.getenv("SENSOR_NAME");
    private final String THING_NAME = System.getenv("THING_NAME");
    private final Region THING_REGION = Region.of(System.getenv("THING_REGION"));
    private final IotDataPlaneClient iotDataClient = IotDataPlaneClient.builder()
            .region(THING_REGION)
            .build();
    private final Gson gson = new Gson();

    private enum PowerState {
        ON,
        OFF
    }

    private enum DetectionState {
        DETECTED,
        NOT_DETECTED,
    }

    private JsonObject getThingState(String thingName) {
        GetThingShadowRequest shadowRequest = GetThingShadowRequest.builder()
                .thingName(thingName).build();
        GetThingShadowResponse response = iotDataClient.getThingShadow(shadowRequest);
        JsonObject thingData = gson.fromJson(
                new InputStreamReader(response.payload().asInputStream()), JsonObject.class);

        return thingData.getAsJsonObject("state");
    }

    private Context getSensorContext() {
        JsonObject sensorState = getThingState(SENSOR_NAME);
        var detectionState = sensorState.get("double_tap").getAsBoolean()
                ? DetectionState.DETECTED
                : DetectionState.NOT_DETECTED;

        return Context.builder()
                .property(Property.builder()
                        .namespace("Alexa.MotionSensor")
                        .name("detectionState")
                        .value(detectionState)
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

    private Context getThingContext() {
        JsonObject thingState = getThingState(THING_NAME);
        boolean isOn = thingState.getAsJsonObject("desired")
                .get("LEDOn").getAsInt() != 0;

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

        Context context;
        if (endpointId.equals("demo-device-01")) {
            context = getThingContext();
        } else if (endpointId.equals("door-sensor-01")) {
            context = getSensorContext();
        } else {
            throw new InvalidDirectiveException(DirectiveName.fromDirective(request),
                    "Unsupported device '" + endpointId + "'");
        }

        return SkillResponse.<Void>builder()
                .context(context)
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
