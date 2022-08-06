package edu.uwb.stmcapstone2022.alexaiot.providers;

import edu.uwb.stmcapstone2022.alexaiot.DirectiveHandler;
import edu.uwb.stmcapstone2022.alexaiot.DirectiveHandlerProvider;
import edu.uwb.stmcapstone2022.alexaiot.DirectiveName;
import edu.uwb.stmcapstone2022.alexaiot.alexa.model.Directive;
import edu.uwb.stmcapstone2022.alexaiot.alexa.model.Event;
import edu.uwb.stmcapstone2022.alexaiot.alexa.model.SkillResponse;
import edu.uwb.stmcapstone2022.alexaiot.alexa.model.directive.AlexaAuthorizationAcceptGrant;
import lombok.var;

import java.util.Map;

public final class AlexaAuthorizationProvider implements DirectiveHandlerProvider {
    private SkillResponse<Void> acceptGrant(Directive<AlexaAuthorizationAcceptGrant> request) {
        var event = Event.<Void>builder()
                .header(request.getHeader().toResponseBuilder()
                        .namespace("Alexa.Authorization")
                        .name("AcceptGrant.Response")
                        .build())
                .build();

        return SkillResponse.<Void>builder()
                .event(event)
                .build();
    }

    public Map<DirectiveName, DirectiveHandler<?>> advertiseHandlers() {
        return Map.of(
                new DirectiveName("Alexa.Authorization", "AcceptGrant"),
                new DirectiveHandler<>(AlexaAuthorizationAcceptGrant.class, this::acceptGrant));
    }
}
