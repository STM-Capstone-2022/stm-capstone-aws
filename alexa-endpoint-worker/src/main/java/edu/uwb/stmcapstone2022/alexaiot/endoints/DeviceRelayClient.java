package edu.uwb.stmcapstone2022.alexaiot.endoints;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import software.amazon.awssdk.core.SdkBytes;
import software.amazon.awssdk.services.iotdataplane.IotDataPlaneClient;
import software.amazon.awssdk.services.iotdataplane.model.GetThingShadowRequest;
import software.amazon.awssdk.services.iotdataplane.model.UpdateThingShadowRequest;

import java.io.InputStreamReader;

import static java.util.Collections.singletonMap;

@Slf4j
public final class DeviceRelayClient implements ThingClient {
    private final IotDataPlaneClient dataPlaneClient;
    private final String thingName;
    private final String relayName;
    private final Gson gson = new Gson();

    public DeviceRelayClient(IotDataPlaneClient dataPlaneClient, String thingName, String relayName) {
        this.dataPlaneClient = dataPlaneClient;
        this.thingName = thingName;
        this.relayName = relayName;
    }

    private JsonObject getDesiredState() {
        val request = GetThingShadowRequest.builder()
                .thingName(thingName).build();
        val response = dataPlaneClient.getThingShadow(request);

        log.info("getThingState({}): {}", thingName, response.payload().asUtf8String());
        return gson.fromJson(new InputStreamReader(response.payload().asInputStream()), JsonObject.class)
                .getAsJsonObject("state").getAsJsonObject("desired");
    }

    private SdkBytes buildUpdateRequest(String relayValue) {
        val responseObject
                = singletonMap("state", singletonMap("desired", singletonMap(relayName, relayValue)));
        return SdkBytes.fromUtf8String(gson.toJson(responseObject));
    }

    @Override
    public void turnOn() {
        log.info("TURNING ON relay device [name={},relay={}]",
                thingName, relayName);
        val request = UpdateThingShadowRequest.builder()
                .thingName(thingName)
                .payload(buildUpdateRequest("1"))
                .build();

        val response = dataPlaneClient.updateThingShadow(request);
        log.info("TURNED ON relay device [name={},relay={}]: {}",
                thingName, relayName, response.payload().asUtf8String());
    }

    @Override
    public void turnOff() {
        log.info("TURNING OFF relay device [name={},relay={}]",
                thingName, relayName);
        val request = UpdateThingShadowRequest.builder()
                .thingName(thingName)
                .payload(buildUpdateRequest("0"))
                .build();

        val response = dataPlaneClient.updateThingShadow(request);
        log.info("TURNED OFF relay device [name={},relay={}]: {}",
                thingName, relayName, response.payload().asUtf8String());
    }

    @Override
    public boolean isPoweredOn() {
        val powerState = getDesiredState().get(relayName).getAsJsonPrimitive();
        if (powerState.isString()) {
            return powerState.getAsString().equals("1");
        } else {
            return powerState.getAsBoolean();
        }
    }
}
