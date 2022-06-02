package edu.uwb.stmcapstone2022.alexaiot.alexa.model.event;

import com.google.gson.annotations.SerializedName;
import lombok.*;
import lombok.experimental.Accessors;

import java.util.List;
import java.util.Map;

@Builder
@Getter
public class AlexaDiscoveryDiscoverResponse {
    @lombok.Builder
    @Getter
    public static class Endpoint {
        @NonNull private final String endpointId;
        @NonNull private final String manufacturerName;
        @NonNull private final String description;
        @NonNull private final String friendlyName;
        @NonNull private final List<DisplayCategory> displayCategories;
        private final AdditionalAttributes additionalAttributes;
        @Singular
        @NonNull private final List<Capability> capabilities;
        private final List<Connections> connections;
        private final Relationships relationships;
        private final Map<String, String> cookies;
    }

    @lombok.Builder
    @Getter
    public static class AdditionalAttributes {
        private final String manufacturer;
        private final String model;
        private final String serialNumber;
        private final String firmwareVersion;
        private final String softwareVersion;
        private final String customIdentifier;
    }

    @lombok.Builder
    @Getter
    public static class Capability {
        @NonNull private final String type;
        @SerializedName("interface")
        @NonNull private final String interfaceName;
        private final String instance;
        @NonNull private final String version;
        @NonNull private final Properties properties;
        private final Object capabilityResources;
        private final Object configuration;
        private final Object configurations;
        private final Semantics semantics;
        private final List<VerificationsRequired> verificationsRequired;
        private final List<DirectiveConfigurations> directiveConfigurations;
    }

    @lombok.Builder
    @Getter
    public static class Properties {
        private final List<Object> supported;
        private final Boolean proactivelyReported;
        private final Boolean retrievable;
    }

    @lombok.Builder
    @Getter
    public static class Semantics {
        private final List<ActionMappings> actionMappings;
        private final List<StateMappings> stateMappings;

        @lombok.Builder
        @Getter
        public static class ActionMappings {
            @SerializedName("@type")
            @NonNull private final String type;
            private final List<Object> actions;
            @NonNull private final Directive directive;

            @lombok.Builder
            @Getter
            public static class Directive {
                @NonNull private final String name;
                private final Object payload;
            }
        }

        @lombok.Builder
        @Getter
        public static class StateMappings {
            @SerializedName("@type")
            @NonNull private final String type;
            @NonNull private final List<Object> states;
            private final Object value;
            private final Object range;
        }
    }

    @lombok.Builder
    @Getter
    public static class VerificationsRequired {
        public enum Method {
            @SerializedName("Confirmation")
            CONFIRMATION,
        }

        @NonNull private final String directive;
        @NonNull private final List<Method> methods;
    }

    @lombok.Builder
    @Getter
    public static class DirectiveConfigurations {
        @lombok.Builder
        @Getter
        public static class AuthenticationConfidenceLevel {
            public static final Integer LEVEL_USE_PROFILE_PIN = 300;
            public static final Integer LEVEL_USE_OTP = 400;

            @AllArgsConstructor
            @Getter
            public static class CustomPolicy {
                private final String policyName;
            }

            @NonNull private final Integer level;
            private final CustomPolicy customPolicy;
        }

        @NonNull private final List<String> directives;
        private final AuthenticationConfidenceLevel requestedAuthenticationConfidenceLevel;
    }

    @lombok.Builder
    @Getter
    public static class Connections {
        public enum Type {
            MATTER,
            TCP_IP,
            ZIGBEE,
            ZWAVE,
            UNKNOWN,
        }

        @NonNull private final Type type;
        private final String macAddress;
        private final String homeId;
        private final String nodeId;
        private final Integer matterVendorId;
        private final Integer matterProductId;
        private final String macNetworkInterface;
        private final String value;
    }

    @lombok.Builder
    @Getter
    public static class Relationships {
        @lombok.Builder
        @Getter
        public static class IsConnectedBy {
            @NonNull private final String endpointId;
        }

        @lombok.Builder
        @Getter
        public static class Alexa {
            @lombok.Builder
            @Getter
            public static class IsPartOf {
                @NonNull private final String endpointId;
            }

            @NonNull private final IsPartOf isPartOf;
        }

        private final IsConnectedBy isConnectedBy;
        private final Alexa alexa;
    }

    public enum DisplayCategory {
        /**
         * Combination of devices set to a specific state. Use activity triggers for scenes when the state changes must occur in a specific order. For example, for a scene named "watch Netflix" you might power on the TV first, and then set the input to HDMI1.
         */
        ACTIVITY_TRIGGER,

        /**
         * Device that cools the air in interior spaces.
         */
        AIR_CONDITIONER,

        /**
         * Device that emits pleasant odors and masks unpleasant odors in interior spaces.
         */
        AIR_FRESHENER,

        /**
         * Device that improves the quality of air in interior spaces.
         */
        AIR_PURIFIER,

        /**
         * Smart device in an automobile, such as a dash camera.
         */
        AUTO_ACCESSORY,

        /**
         * Speaker that connects to an audio source over Bluetooth.
         */
        BLUETOOTH_SPEAKER,

        /**
         * Security device with video or photo functionality.
         */
        CAMERA,

        /**
         * Religious holiday decoration that often contains lights.
         */
        CHRISTMAS_TREE,

        /**
         * Device that makes coffee.
         */
        COFFEE_MAKER,

        /**
         * Non-mobile computer, such as a desktop computer.
         */
        COMPUTER,

        /**
         * Endpoint that detects and reports changes in contact between two surfaces.
         */
        CONTACT_SENSOR,

        /**
         * Appliance that cleans dishes.
         */
        DISHWASHER,

        /**
         * Endpoint that allows entrance to a building, room, closet, cupboard, or vehicle.
         */
        DOOR,

        /**
         * Smart doorbell.
         */
        DOORBELL,

        /**
         * Appliance that dries wet clothing.
         */
        DRYER,

        /**
         * Window covering, such as blinds or shades, on the outside of a structure.
         */
        EXTERIOR_BLIND,

        /**
         * Device for cooling or ventilation.
         */
        FAN,

        /**
         * Video game console, such as Microsoft Xbox or Nintendo Switch
         */
        GAME_CONSOLE,

        /**
         * Endpoint that allows vehicles to enter a garage. Garage doors must implement the ModeController interface to open and close the door.
         */
        GARAGE_DOOR,

        /**
         * Wearable device that transmits audio directly into the ear.
         */
        HEADPHONES,

        /**
         * Smart-home hub.
         */
        HUB,

        /**
         * Window covering, such as blinds or shades, on the inside of a structure.
         */
        INTERIOR_BLIND,

        /**
         * Laptop or other mobile computer.
         */
        LAPTOP,

        /**
         * Light source or fixture.
         */
        LIGHT,

        /**
         * Microwave oven cooking appliance.
         */
        MICROWAVE,

        /**
         * Mobile phone.
         */
        MOBILE_PHONE,

        /**
         * Endpoint that detects and reports movement in an area.
         */
        MOTION_SENSOR,

        /**
         * Network-connected music system.
         */
        MUSIC_SYSTEM,

        /**
         * Network router.
         */
        NETWORK_HARDWARE,

        /**
         * Endpoint that doesn't belong to one of the other categories.
         */
        OTHER,

        /**
         * Oven cooking appliance.
         */
        OVEN,

        /**
         * Non-mobile phone, such as landline or an IP phone.
         */
        PHONE,

        /**
         * Device that prints.
         */
        PRINTER,

        /**
         * Network router.
         */
        ROUTER,

        /**
         * Combination of devices set to a specific state. Use scene triggers for scenes when the order of the state change isn't important. For example, for a scene named "bedtime" you might turn off the lights and lower the thermostat, in any order.
         */
        SCENE_TRIGGER,

        /**
         * Projector screen.
         */
        SCREEN,

        /**
         * Security panel.
         */
        SECURITY_PANEL,

        /**
         * Security system.
         */
        SECURITY_SYSTEM,

        /**
         * Electric cooking appliance that sits on a countertop, cooks at low temperatures, and is often shaped like a cooking pot.
         */
        SLOW_COOKER,

        /**
         * Endpoint that locks.
         */
        SMARTLOCK,

        /**
         * Module plugged into an existing electrical outlet, and then has a device plugged into it. For example, a user can plug a smart plug into an outlet, and then plug a lamp into the smart plug. A smart plug can control a variety of devices.
         */
        SMARTPLUG,

        /**
         * Speaker or speaker system.
         */
        SPEAKER,

        /**
         * Streaming device, such as Apple TV, Chromecast, or Roku.
         */
        STREAMING_DEVICE,

        /**
         * Switch wired directly to the electrical system. A switch can control a variety of devices.
         */
        SWITCH,

        /**
         * Tablet computer.
         */
        TABLET,
        /**

         * Endpoint that reports temperature, but doesn't control it. The temperature data of the endpoint doesn't appear in the Alexa app. If your endpoint also controls temperature, use THERMOSTAT instead.
         */
        TEMPERATURE_SENSOR,

        /**
         * Endpoint that controls temperature, stand-alone air conditioners, or heaters with direct temperature control. If your endpoint senses temperature but doesn't control it, use TEMPERATURE_SENSOR instead.
         */
        THERMOSTAT,

        /**
         * Television.
         */
        TV,

        /**
         * Vacuum cleaner.
         */
        VACUUM_CLEANER,

        /**
         * Motor vehicle (automobile, car).
         */
        VEHICLE,

        /**
         * Appliance that cleans clothing.
         */
        WASHER,

        /**
         * Device that heats water, often consisting of a large tank.
         */
        WATER_HEATER,

        /**
         * Network-connected wearable device, such as an Apple Watch, Fitbit, or Samsung Gear.
         */
        WEARABLE
    }

    @Singular
    private final List<Endpoint> endpoints;
}
