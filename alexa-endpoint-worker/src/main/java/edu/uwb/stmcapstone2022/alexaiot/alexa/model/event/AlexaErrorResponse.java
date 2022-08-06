package edu.uwb.stmcapstone2022.alexaiot.alexa.model.event;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class AlexaErrorResponse {
    public enum Type {
        /**
         * The endpoint can't perform the requested operation because the endpoint is already in operation.
         */
        ALREADY_IN_OPERATION,

        /**
         * The bridge is unreachable or offline. For example, the bridge might be turned off, disconnected from the customer's local area network, or connectivity between the bridge and the device cloud might have been lost. When you respond to a ReportState directive, there might be times when you should return a StateReport instead of this error. For details, see Alexa.EndpointHealth.
         */
        BRIDGE_UNREACHABLE,

        /**
         * The user can't control the device over the internet, and should control the device manually instead.
         */
        CLOUD_CONTROL_DISABLED,

        /**
         * The endpoint can't handle the directive because it's performing another action. The action might be a request to Alexa or a manual interaction.
         */
        ENDPOINT_BUSY,

        /**
         * The endpoint can't handle the directive because the battery power is too low. See an example here.
         */
        ENDPOINT_LOW_POWER,

        /**
         * The endpoint is unreachable or offline. For example, the endpoint might be turned off, disconnected from the customer's local area network, or connectivity between the endpoint and bridge or the endpoint and the device cloud might have been lost. When you respond to a ReportState directive, there might be times when you should return a StateReport instead of this error. For more information, see Alexa.EndpointHealth.
         */
        ENDPOINT_UNREACHABLE,

        /**
         * The authorization credential provided by Alexa has expired. For example, the OAuth2 access token for the customer has expired.
         */
        EXPIRED_AUTHORIZATION_CREDENTIAL,

        /**
         * The endpoint can't handle the directive because it's firmware is out of date.
         */
        FIRMWARE_OUT_OF_DATE,

        /**
         * The endpoint can't handle the directive because it has experienced a hardware malfunction.
         */
        HARDWARE_MALFUNCTION,

        /**
         * Alexa doesn't have permissions to perform the specified action on the endpoint.
         */
        INSUFFICIENT_PERMISSIONS,

        /**
         * An error occurred that isn't described by one of the other error types. For example, a runtime exception occurred. Amazon recommends that you always send a more specific error type, if possible.
         */
        INTERNAL_ERROR,

        /**
         * The authorization credential provided by Alexa is invalid. For example, the OAuth2 access token isn't valid for the customer's device cloud account.
         */
        INVALID_AUTHORIZATION_CREDENTIAL,

        /**
         * The directive isn't supported by the skill, or the directive is malformed.
         */
        INVALID_DIRECTIVE,

        /**
         * The directive contains a value that's not valid for the target endpoint. For example, an invalid heating mode, channel, or program value.
         */
        INVALID_VALUE,

        /**
         * The endpoint doesn't exist, or no longer exists.
         */
        NO_SUCH_ENDPOINT,

        /**
         * The endpoint can't handle the directive because it's in a calibration phase, such as warming up, or a user configuration isn't set up yet on the device.
         */
        NOT_CALIBRATED,

        /**
         * The endpoint can't set the device to the specified value because of its current mode of operation. When you send this error response, include a field in the payload named currentDeviceMode to indicate why the device can't be set to the new value. Valid values are COLOR, ASLEEP, NOT_PROVISIONED, OTHER. See an example here.
         */
        NOT_SUPPORTED_IN_CURRENT_MODE,

        /**
         * The endpoint can't fulfill the request due to the current battery state. When you send this error response, include the currentChargeState property in the payload to indicate the battery state. You can optionally include the CurrentChargeLevelInPercentage property to indicate the battery level in percentage. See an example here.
         */
        NOT_SUPPORTED_WITH_CURRENT_BATTERY_CHARGE_STATE,

        /**
         * The endpoint isn't in operation. For example, a smart home skill can return a NOT_IN_OPERATION error when it receives a RESUME directive but the endpoint is the OFF mode.
         */
        NOT_IN_OPERATION,

        /**
         * The endpoint can't handle the directive because it doesn't support the requested power level.
         */
        POWER_LEVEL_NOT_SUPPORTED,

        /**
         * The requests exceed the maximum rate at which an endpoint or bridge can process directives.
         */
        RATE_LIMIT_EXCEEDED,

        /**
         * The endpoint can't be set to the specified value because it's outside the acceptable temperature range. When you send this error response, optionally include a validRange object in the payload that indicates the acceptable temperature range. See an example here.
         */
        TEMPERATURE_VALUE_OUT_OF_RANGE,

        /**
         * The number of allowed failed attempts, such as when entering a password, has been exceeded.
         */
        TOO_MANY_FAILED_ATTEMPTS,

        /**
         * The endpoint can't be set to the specified value because it's outside the acceptable range. For example, you can use this error when a customer requests a percentage value over 100. For temperature values, use TEMPERATURE_VALUE_OUT_OF_RANGE instead. When you send this error response, optionally include a validRange object in the payload that indicates the acceptable range. See an example here.
         */
        VALUE_OUT_OF_RANGE,
    }

    private final Type type;
    private final String message;
}