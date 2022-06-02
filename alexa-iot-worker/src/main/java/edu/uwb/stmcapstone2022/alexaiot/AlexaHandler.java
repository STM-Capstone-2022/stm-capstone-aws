package edu.uwb.stmcapstone2022.alexaiot;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestStreamHandler;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.annotations.SerializedName;
import edu.uwb.stmcapstone2022.alexaiot.model.*;
import edu.uwb.stmcapstone2022.alexaiot.model.request.AlexaAuthorizationRequest;
import edu.uwb.stmcapstone2022.alexaiot.model.request.AlexaDiscoveryRequest;
import edu.uwb.stmcapstone2022.alexaiot.model.request.AlexaPowerControllerRequest;
import edu.uwb.stmcapstone2022.alexaiot.model.request.AlexaReportStateRequest;
import edu.uwb.stmcapstone2022.alexaiot.model.response.AlexaDiscoveryResponse;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import lombok.var;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Map;

@Slf4j
public class AlexaHandler implements RequestStreamHandler {
    private static final String BUCKET_NAME = System.getenv("BUCKET_NAME");
    private static final List<String> POWER_CONTROLS = List.of("TurnOn", "TurnOff");
    private final Gson gson = new GsonBuilder()
            .setPrettyPrinting()
            .create();

    private final S3Client s3Client = S3Client.create();

    @Override
    public void handleRequest(InputStream inputStream, OutputStream outputStream, Context context) throws IOException {

        AlexaDirective directive = gson.fromJson(
                gson.fromJson(new InputStreamReader(inputStream), JsonObject.class)
                        .getAsJsonObject("directive"),
                AlexaDirective.class);

        log.info("Request: {}", gson.toJson(directive));

        Object response;
        if (directive.getHeader().getNamespace().equals("Alexa.Discovery")
                && directive.getHeader().getName().equals("Discover")) {
            log.debug("Discover Request: {}", gson.toJson(directive));
            response = handleDiscovery(SmartHomeRequest.fromDirective(
                            directive, gson.fromJson(directive.getPayload(), AlexaDiscoveryRequest.class)));
        } else if (directive.getHeader().getNamespace().equals("Alexa.PowerController")
                && POWER_CONTROLS.contains(directive.getHeader().getName())) {
            log.debug("TurnOn/TurnOff request: {}", gson.toJson(directive));
            response = handlePowerControl(SmartHomeRequest.fromDirective(
                    directive, gson.fromJson(directive.getPayload(), AlexaPowerControllerRequest.class)));
        } else if (directive.getHeader().getNamespace().equals("Alexa")
        && directive.getHeader().getName().equals("ReportState")) {
            log.debug("State Report request: {}", gson.toJson(directive));
            response = handleStateReport(SmartHomeRequest.fromDirective(
                    directive, gson.fromJson(directive.getPayload(), AlexaReportStateRequest.class)));
        } else if (directive.getHeader().getNamespace().equals("Alexa.Authorization")
                && directive.getHeader().getName().equals("AcceptGrant")) {
            log.debug("Authorization request: {}", gson.toJson(directive));
            response = authorizeRequest(SmartHomeRequest.fromDirective(
                            directive, gson.fromJson(directive.getPayload(), AlexaAuthorizationRequest.class)));
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
                                .manufacturerName("Smart Device Company")
                                .friendlyName("Livingroom Lamp")
                                .description("Virtual smart light bulb")
                                .displayCategories(List.of(AlexaDiscoveryResponse.DisplayCategory.LIGHT))
                                .additionalAttributes(AlexaDiscoveryResponse.AdditionalAttributes.builder()
                                        .manufacturer("Sample Manufacturer")
                                        .model("Sample Model")
                                        .serialNumber("U11112233456")
                                        .firmwareVersion("1.24.2546")
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
                        .name("Discover.Response")
                        .messageId(request.getHeader().getMessageId() + "-R")
                        .build())
                .payload(response)
                .build();
        log.info("Discover response: {}", gson.toJson(event));

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
                .header(request.getHeader().toResponseBuilder()
                        .namespace("Alexa")
                        .name("Response")
                        .build())
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
                .header(request.getHeader().toResponseBuilder()
                        .namespace("Alexa")
                        .name("Response")
                        .build())
                .payload(new Object());

        return Map.of("event", event);
    }

    @Builder(builderClassName = "Builder")
    @Getter
    @ToString
    private static class EnvDataPayload {
        private final double temp_0_c;
        private final double rh_pct;
        private final double temp_1_c;
        private final double baro_mbar;
        @SerializedName("fake door knock")
        private final float fake_door_knock;
    }

    public Object handleStateReport(SmartHomeRequest<AlexaReportStateRequest> request) {
        GetObjectRequest objRequest = GetObjectRequest.builder()
                .bucket(BUCKET_NAME)
                .key("envData.json")
                .build();

        var objectResponse = s3Client.getObject(objRequest);
        try (var objectReader = new InputStreamReader(objectResponse)) {
            EnvDataPayload object = gson.fromJson(objectReader, EnvDataPayload.class);

            log.info("Object: {}", object);
            throw new UnsupportedOperationException("No idea how to do a handleStateReport /shrug");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
