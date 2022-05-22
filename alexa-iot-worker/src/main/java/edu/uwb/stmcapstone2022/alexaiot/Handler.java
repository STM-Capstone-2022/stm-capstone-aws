package edu.uwb.stmcapstone2022.alexaiot;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import software.amazon.awssdk.core.SdkBytes;
import software.amazon.awssdk.services.iotdataplane.IotDataPlaneClient;
import software.amazon.awssdk.services.iotdataplane.model.UpdateThingShadowRequest;
import software.amazon.awssdk.services.iotdataplane.model.UpdateThingShadowResponse;

import java.util.Map;

public class Handler implements RequestHandler<Object, Object> {
    private final IotDataPlaneClient iotDataClient = IotDataPlaneClient.create();
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public Object handleRequest(Object event, Context context) {
        UpdateThingShadowRequest request;
        try {
            request = UpdateThingShadowRequest.builder()
                    .thingName(System.getenv("THING_NAME"))
                    .payload(SdkBytes.fromByteArray(objectMapper.writeValueAsBytes(event)))
                    .build();
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

        UpdateThingShadowResponse response = iotDataClient.updateThingShadow(request);
        return Map.of(
                "status", "OK",
                "requestId", response.responseMetadata().requestId()
        );
    }
}
