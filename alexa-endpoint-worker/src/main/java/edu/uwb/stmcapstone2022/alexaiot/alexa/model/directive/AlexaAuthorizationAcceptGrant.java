package edu.uwb.stmcapstone2022.alexaiot.alexa.model.directive;

import com.google.gson.annotations.SerializedName;
import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;

@Builder
@Getter
public class AlexaAuthorizationAcceptGrant {
    @lombok.Builder
    @Getter
    public static class Grant {
        public enum Type {
            @SerializedName("OAuth2.AuthorizationCode")
            OAUTH2_AUTHORIZATION_CODE
        }

        @NonNull private final Type type;
        @NonNull private final String code;
    }

    @lombok.Builder
    @Getter
    public static class Grantee {
        public enum Type {
            @SerializedName("BearerToken")
            BEARER_TOKEN
        }

        @NonNull private final Type type;
        @NonNull private final String token;
    }

    @NonNull private final Grant grant;
    @NonNull private final Grantee grantee;
}
