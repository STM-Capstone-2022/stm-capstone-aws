package edu.uwb.stmcapstone2022.alexaiot.model.response;

import com.google.gson.annotations.SerializedName;
import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;

import java.util.List;
import java.util.Map;

@Builder(builderClassName = "Builder")
@Getter
public class AlexaDiscoveryResponse {
    @lombok.Builder(builderClassName = "Builder")
    @Getter
    public static class Endpoint {
        @NonNull private final String endpointId;
        @NonNull private final String manufacturerName;
        @NonNull private final String description;
        @NonNull private final String friendlyName;
        @NonNull private final List<DisplayCategory> displayCategories;
        @NonNull private final AdditionalAttributes additionalAttributes;
        @NonNull private final List<Capability> capabilities;
        private final List<Connection> connections;
        private final Relationships relationships;
        private final Map<String, String> cookie;
    }

    @SuppressWarnings("unused")
    public enum DisplayCategory {
        ACTIVITY_TRIGGER,
        AIR_CONDITIONER,
        AIR_FRESHENER,
        AIR_PURIFIER,
        AUTO_ACCESSORY,
        BLUETOOTH_SPEAKER,
        CAMERA,
        CHRISTMAS_TREE,
        COFFEE_MAKER,
        COMPUTER,
        CONTACT_SENSOR,
        DISHWASHER,
        DOOR,
        DOORBELL,
        DRYER,
        EXTERIOR_BLIND,
        FAN,
        GAME_CONSOLE,
        GARAGE_DOOR,
        HEADPHONES,
        HUB,
        INTERIOR_BLIND,
        LAPTOP,
        LIGHT,
        MICROWAVE,
        MOBILE_PHONE,
        MOTION_SENSOR,
        MUSIC_SYSTEM,
        NETWORK_HARDWARE,
        OTHER,
        OVEN,
        PHONE,
        PRINTER,
        ROUTER,
        SCENE_TRIGGER,
        SCREEN,
        SECURITY_PANEL,
        SECURITY_SYSTEM,
        SLOW_COOKER,
        SMARTLOCK,
        SMARTPLUG,
        SPEAKER,
        STREAMING_DEVICE,
        SWITCH,
        TABLET,
        TEMPERATURE_SENSOR,
        THERMOSTAT,
        TV,
        VACUUM_CLEANER,
        VEHICLE,
        WASHER,
        WATER_HEATER,
        WEARABLE
    }

    @lombok.Builder(builderClassName = "Builder")
    @Getter
    public static class AdditionalAttributes {
        private final String manufacturer;
        private final String model;
        private final String serialNumber;
        private final String firmwareVersion;
        private final String softwareVersion;
        private final String customIdentifier;
    }

    @lombok.Builder(builderClassName = "Builder")
    @Getter
    public static class Capability {
        @NonNull private final String type;
        @SerializedName("interface")
        @NonNull private final String interfaceValue;
        private final String instance;
        @NonNull private final String version;
        private final Properties properties;
        private final Object capabilityResources;
        private final Object configuration;
        private final Object configurations;
        private final Semantics semantics;
        private final List<VerificationsRequired> verificationsRequired;
        private final List<DirectiveConfigurations> directiveConfigurations;
    }

    @lombok.Builder(builderClassName = "Builder")
    @Getter
    public static class Properties {
        private final List<Object> supported;
        private final Boolean proactivelySupported;
        private final Boolean retrievable;
    }

    @lombok.Builder(builderClassName = "Builder")
    @Getter
    public static class Semantics {
        // TODO
    }

    @lombok.Builder(builderClassName = "Builder")
    @Getter
    public static class VerificationsRequired {
        @NonNull private final String directive;
        @NonNull private final List<Object> methods;
    }

    @lombok.Builder(builderClassName = "Builder")
    @Getter
    public static class DirectiveConfigurations {
        @NonNull private List<String> directives;
        private final Object requestedAuthenticationConfidenceLevel; // TODO
    }

    @lombok.Builder(builderClassName = "Builder")
    @Getter
    public static class Connection {
        @NonNull private final String type;
        private final String macAddress;
        private final String homeId;
        private final String nodeId;
        private final Integer matterDiscriminator;
        // TODO ...

        private final String value;
    }

    @lombok.Builder(builderClassName = "Builder")
    @Getter
    public static class Relationships {
        // TODO fill out
    }

    private final List<Endpoint> endpoints; // TODO

}
