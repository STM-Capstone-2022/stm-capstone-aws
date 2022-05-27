package edu.uwb.stmcapstone2022.alexaiot;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import lombok.extern.slf4j.Slf4j;
import software.amazon.awssdk.core.SdkBytes;
import software.amazon.awssdk.services.iotdataplane.IotDataPlaneClient;
import software.amazon.awssdk.services.iotdataplane.model.UpdateThingShadowRequest;
import software.amazon.awssdk.services.iotdataplane.model.UpdateThingShadowResponse;

import java.nio.charset.StandardCharsets;
import java.util.Map;

@Slf4j
public class Handler implements RequestHandler<Object, Object> {
    private final IotDataPlaneClient iotDataClient = IotDataPlaneClient.create();
    private final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    @Override
    public Object handleRequest(Object event, Context context) {
        log.debug("New request: {}", gson.toJson(event));

        UpdateThingShadowRequest request;
        request = UpdateThingShadowRequest.builder()
                .thingName(System.getenv("THING_NAME"))
                .payload(SdkBytes.fromByteArray(gson.toJson(event).getBytes(StandardCharsets.UTF_8)))
                .build();

        UpdateThingShadowResponse response = iotDataClient.updateThingShadow(request);
        return Map.of(
                "status", "OK",
                "requestId", response.responseMetadata().requestId()
        );
    }
}
