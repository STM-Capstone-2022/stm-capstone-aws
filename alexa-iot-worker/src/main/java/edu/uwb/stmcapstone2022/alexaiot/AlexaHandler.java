package edu.uwb.stmcapstone2022.alexaiot;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestStreamHandler;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import edu.uwb.stmcapstone2022.alexaiot.model.*;
import edu.uwb.stmcapstone2022.alexaiot.model.request.AlexaAuthorizationRequest;
import edu.uwb.stmcapstone2022.alexaiot.model.request.AlexaDiscoveryRequest;
import edu.uwb.stmcapstone2022.alexaiot.model.request.AlexaPowerControllerRequest;
import edu.uwb.stmcapstone2022.alexaiot.model.response.AlexaDiscoveryResponse;
import lombok.extern.slf4j.Slf4j;
import lombok.var;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Map;

@Slf4j
public class AlexaHandler implements RequestStreamHandler {
    private static final List<String> POWER_CONTROLS = List.of("TurnOn", "TurnOff");
    private final Gson gson = new GsonBuilder()
            .setPrettyPrinting()
            .create();
    @Override
    public void handleRequest(InputStream inputStream, OutputStream outputStream, Context context) throws IOException {

        AlexaDirective directive = gson.fromJson(
                gson.fromJson(new InputStreamReader(inputStream), JsonObject.class)
                        .getAsJsonObject("directive"),
                AlexaDirective.class);

        Object response;
        if (directive.getHeader().getNamespace().equals("Alexa.Discovery")
                && directive.getHeader().getName().equals("Discover")) {
            log.debug("Discovery Request: {}", gson.toJson(directive));
            response = handleDiscovery(SmartHomeRequest.fromDirective(
                            directive, gson.fromJson(directive.getPayload(), AlexaDiscoveryRequest.class))
            );
        } else if (directive.getHeader().getNamespace().equals("Alexa.PowerController")
                && POWER_CONTROLS.contains(directive.getHeader().getName())) {
            log.debug("TurnOn/TurnOff request: {}", gson.toJson(directive));
            response = handlePowerControl(SmartHomeRequest.fromDirective(
                            directive, gson.fromJson(directive.getPayload(), AlexaPowerControllerRequest.class))
            );
        } else if (directive.getHeader().getNamespace().equals("Alexa.Authorization")
                && directive.getHeader().getName().equals("AcceptGrant")) {
            log.debug("Authorization request: {}", gson.toJson(directive));
            response = authorizeRequest(SmartHomeRequest.fromDirective(
                            directive, gson.fromJson(directive.getPayload(), AlexaAuthorizationRequest.class))
            );
        } else {
            throw new UnsupportedOperationException(String.format("Unsupported directive %s from interface %s",
                    directive.getHeader().getName(),
                    directive.getHeader().getNamespace()));
        }

        outputStream.write(gson.toJson(response).getBytes(StandardCharsets.UTF_8));
        outputStream.flush();
    }

    private Object handleDiscovery(SmartHomeRequest<AlexaDiscoveryRequest> request) {
        var response = AlexaDiscoveryResponse.builder()
                .endpoints(List.of(
                        AlexaDiscoveryResponse.Endpoint.builder()
                                .endpointId("sample-bulb-01")
                                .manufacturerName("Smarter Device Company")
                                .friendlyName("Livingroom Lamp")
                                .description("Virtual smart LED")
                                .displayCategories(List.of(AlexaDiscoveryResponse.DisplayCategory.LIGHT))
                                .additionalAttributes(AlexaDiscoveryResponse.AdditionalAttributes.builder()
                                        .manufacturer("Sample Manufacturer")
                                        .model("Sample Model")
                                        .serialNumber("U11112233456")
                                        .firmwareVersion("1.24.256")
                                        .softwareVersion("1.036")
                                        .customIdentifier("Sample custom ID")
                                        .build())
                                .cookie(Map.of(
                                        "key1", "Arbitrary key/value pairs for skill to reference this endpoint",
                                        "key2", "There can be multiple entries",
                                        "key3", "but they should only be used for reference purposes",
                                        "key4", "this is not a suitable place for endpoint state."))
                                .capabilities(List.of(
                                        AlexaDiscoveryResponse.Capability.builder()
                                                .interfaceValue("Alexa.PowerController")
                                                .version("3")
                                                .type("AlexaInterface")
                                                .properties(AlexaDiscoveryResponse.Properties.builder()
                                                        .supported(List.of(
                                                                Map.of("name", "powerState")))
                                                        .retrievable(true)
                                                        .build())
                                                .build(),
                                        AlexaDiscoveryResponse.Capability.builder()
                                                .type("AlexaInterface")
                                                .interfaceValue("Alexa.EndpointHealth")
                                                .version("3.2")
                                                .properties(AlexaDiscoveryResponse.Properties.builder()
                                                        .supported(List.of(
                                                                Map.of("name", "connectivity")))
                                                        .retrievable(true)
                                                        .build())
                                                .build(),
                                        AlexaDiscoveryResponse.Capability.builder()
                                                .type("AlexaInterface")
                                                .interfaceValue("Alexa")
                                                .version("3")
                                                .build()))
                                .build()))
                .build();

        var event = AlexaEvent.<AlexaDiscoveryResponse>builder()
                .header(request.getHeader().toBuilder()
                        .namespace("Alexa")
                        .name("Response")
                        .messageId(request.getHeader().getMessageId() + "-R")
                        .build())
                .payload(response)
                .build();
        log.info("Discovery response: {}", gson.toJson(event));

        return Map.of("event", event);
    }

    private Object handlePowerControl(SmartHomeRequest<AlexaPowerControllerRequest> request) {
        String requestMethod = request.getHeader().getName();
        String requestToken = request.getEndpoint().getScope().getToken();
        String powerResult = null;

        if (requestMethod.equals("TurnOn")) {
            powerResult = "ON";
        } else if (requestMethod.equals("TurnOff")) {
            powerResult = "OFF";
        }

        var now = ZonedDateTime.now();
        var responseContext = AlexaContext.builder()
                .property(AlexaProperty.builder()
                        .namespace("Alexa.PowerController")
                        .name("powerState")
                        .value(powerResult)
                        .timeOfSample(now)
                        .uncertaintyInMilliseconds(50)
                        .build())
                .property(AlexaProperty.builder()
                        .namespace("Alexa.EndpointHealth")
                        .name("connectivity")
                        .value(Map.of("value", "OK"))
                        .timeOfSample(now)
                        .uncertaintyInMilliseconds(50)
                        .build())
                .build();

        var event = AlexaEvent.builder()
                .header(request.getHeader().toResponseHeader())
                .endpoint(AlexaEndpoint.builder()
                        .scope(AlexaScope.builder()
                                .type("BearerToken")
                                .token(requestToken)
                                .build())
                        .endpointId("sample-bulb-01")
                        .build())
                .payload(new Object())
                .build();

        return Map.of(
                "context", responseContext,
                "event", event);
    }

    public Object authorizeRequest(SmartHomeRequest<AlexaAuthorizationRequest> request) {
        // Always accept the request
        var event = AlexaEvent.builder()
                .header(request.getHeader().toResponseHeader())
                .payload(new Object());

        return Map.of("event", event);
    }
}
