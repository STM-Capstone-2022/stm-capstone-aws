package edu.uwb.stmcapstone2022.alexaiot;

import edu.uwb.stmcapstone2022.alexaiot.alexa.model.Directive;
import edu.uwb.stmcapstone2022.alexaiot.alexa.model.Event;
import edu.uwb.stmcapstone2022.alexaiot.alexa.model.SkillResponse;
import edu.uwb.stmcapstone2022.alexaiot.alexa.model.directive.AlexaAuthorizationAcceptGrant;
import lombok.var;

public class AlexaAuthorizationHandler {
    public SkillResponse<Void> handleAcceptGrant(Directive<AlexaAuthorizationAcceptGrant> request) {
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
}
