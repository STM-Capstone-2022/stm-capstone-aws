package edu.uwb.stmcapstone2022.alexaiot;

import com.amazonaws.services.iotdata.AWSIotData;
import com.amazonaws.services.iotdata.AWSIotDataClientBuilder;
import com.amazonaws.services.iotdata.model.UpdateThingShadowRequest;
import com.amazonaws.services.iotdata.model.UpdateThingShadowResult;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.nio.ByteBuffer;
import java.util.Map;

public class Handler implements RequestHandler<Map<String, Object>, Map<String, Object>> {
    private final ObjectMapper mapper = new ObjectMapper();

    public Map<String, Object> handleRequest(Map<String, Object> event, Context context) {
        AWSIotData client = AWSIotDataClientBuilder.defaultClient();

        UpdateThingShadowRequest request;
        try {
            request = new UpdateThingShadowRequest()
                    .withThingName("ikLkaEbPgpQiP1pL")
                    .withPayload(ByteBuffer.wrap(mapper.writeValueAsBytes(event)));
        } catch (JsonProcessingException e) {
            return Map.of(
                    "message", "goodbye, world!",
                    "error", e.getMessage()
            );
        }

        UpdateThingShadowResult result = client.updateThingShadow(request);

        return Map.of(
                "message", "hello, world!",
                "result", result
        );
    }
}