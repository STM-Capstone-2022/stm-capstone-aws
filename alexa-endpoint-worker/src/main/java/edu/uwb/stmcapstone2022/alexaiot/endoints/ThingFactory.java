package edu.uwb.stmcapstone2022.alexaiot.endoints;

import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.iotdataplane.IotDataPlaneClient;

public class ThingFactory {
    private static final String THING_REGION = System.getenv("THING_REGION");
    private static final String RELAY_NAME = System.getenv("RELAY_NAME");
    private final IotDataPlaneClient dataPlaneClient;

    private static final ThingFactory instance = new ThingFactory();

    public static ThingFactory getInstance() {
        return instance;
    }

    private ThingFactory() {
        dataPlaneClient = IotDataPlaneClient.builder()
                .region(Region.of(THING_REGION))
                .build();
    }

    public ThingClient get(String endpointName) {
        switch (endpointName) {
            case "demo-lock-01":
                return new DeviceRelayClient(dataPlaneClient, RELAY_NAME, "RELAY_1_On");
            case "demo-humidifier-01":
                return new DeviceRelayClient(dataPlaneClient, RELAY_NAME, "RELAY_2_On");
            case "demo-light-01":
                return new DeviceRelayClient(dataPlaneClient, RELAY_NAME, "RELAY_3_On");
            case "demo-fan-01":
                return new DeviceRelayClient(dataPlaneClient, RELAY_NAME, "RELAY_4_On");
            default:
                throw new NoSuchEndpointException("No such device: " + endpointName);
        }
    }
}
