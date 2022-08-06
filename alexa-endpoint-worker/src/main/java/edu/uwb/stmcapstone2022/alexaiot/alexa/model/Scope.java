package edu.uwb.stmcapstone2022.alexaiot.alexa.model;

import com.google.gson.annotations.SerializedName;
import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;

@Builder(toBuilder = true)
@Getter
public class Scope {
    public enum Type {
        @SerializedName("BearerToken")
        BEARER_TOKEN,
        @SerializedName("BearerTokenWithPartition")
        BEARER_TOKEN_WITH_PARTITION;
    }

    /**
     * Provides the scope type of the OAuth token.
     */
    @NonNull private final Type type;

    /**
     * OAuth bearer token to identify and access a linked customer account.
     */
    @NonNull private final String token;

    /**
     * Location target for the request, such as a room name or number.
     * Valid for BearerTokenWithPartition only.
     */
    private final String partition;

    /**
     * Unique identifier for the user who made the request. Don't rely on this value to identify users. Use token instead.
     * Valid for BearerTokenWithPartition only.
     */
    private final String userId;
}
