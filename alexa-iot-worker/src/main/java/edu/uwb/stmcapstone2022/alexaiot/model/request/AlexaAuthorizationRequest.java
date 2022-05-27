package edu.uwb.stmcapstone2022.alexaiot.model.request;

import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;

@Builder
@Getter
public class AlexaAuthorizationRequest {
    @Builder
    @Getter
    public static class Grant {
        @NonNull private final String type;
        @NonNull private final String code;
    }

    @Builder
    @Getter
    public static class Grantee {
        @NonNull private final String type;
        @NonNull private final String token;
    }

    @NonNull private final Grant grant;
    @NonNull private final Grantee grantee;
}
