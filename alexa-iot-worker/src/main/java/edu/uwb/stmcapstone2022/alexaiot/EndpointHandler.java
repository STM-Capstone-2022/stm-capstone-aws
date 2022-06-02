package edu.uwb.stmcapstone2022.alexaiot;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestStreamHandler;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import edu.uwb.stmcapstone2022.alexaiot.alexa.model.Directive;
import edu.uwb.stmcapstone2022.alexaiot.alexa.model.SkillRequest;
import edu.uwb.stmcapstone2022.alexaiot.alexa.model.SkillResponse;
import edu.uwb.stmcapstone2022.alexaiot.alexa.model.directive.AlexaAuthorizationAcceptGrant;
import edu.uwb.stmcapstone2022.alexaiot.alexa.model.directive.AlexaDiscoveryDiscover;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import lombok.var;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

@Slf4j
public class EndpointHandler implements RequestStreamHandler {
    private final Gson gson = new GsonBuilder().create();

    private final AlexaDiscoveryHandler discoveryHandler = new AlexaDiscoveryHandler();
    private final AlexaAuthorizationHandler authorizationHandler = new AlexaAuthorizationHandler();
    private final AlexaHandler alexaHandler = new AlexaHandler();
    private final AlexaReportStateHandler reportStateHandler = new AlexaReportStateHandler();

    private static class RawSkillRequest extends SkillRequest<JsonObject> {
        public RawSkillRequest(@NonNull Directive<JsonObject> directive) {
            super(directive);
        }
    }

    @Override
    public void handleRequest(InputStream inputStream, OutputStream outputStream, Context context) throws IOException {
        Directive<JsonObject> directive = gson.fromJson(new InputStreamReader(inputStream), RawSkillRequest.class)
                .getDirective();

        log.info("Directive request: {}", gson.toJson(directive));

        var namespace = directive.getHeader().getNamespace();
        var name = directive.getHeader().getName();

        SkillResponse<?> object;
        if (namespace.equals("Alexa.Discovery") && name.equals("Discover")) {
            object = discoveryHandler.handleDiscover(mapDirective(directive, AlexaDiscoveryDiscover.class));
        } else if (namespace.equals("Alexa.Authorization") && name.equals("AcceptGrant")) {
            object = authorizationHandler.handleAcceptGrant(
                    mapDirective(directive, AlexaAuthorizationAcceptGrant.class));
        } else if (namespace.equals("Alexa.PowerController")) {
            var d = mapDirective(directive, Void.class);
            if (name.equals("TurnOn")) {
                object = alexaHandler.handleTurnOn(d);
            } else if (name.equals("TurnOff")) {
                object = alexaHandler.handleTurnOff(d);
            } else {
                throw new UnsupportedOperationException("Unsupported Directive '" + namespace + "::" + name + "'");
            }
        } else if (namespace.equals("Alexa") && name.equals("ReportState")) {
            object = reportStateHandler.handleReportState(mapDirective(directive, Void.class));
        } else {
            throw new UnsupportedOperationException("Unsupported Directive '" + namespace + "::" + name + "'");
        }

        log.info("Skill response: {}", gson.toJson(object));

        outputStream.write(gson.toJson(object).getBytes(StandardCharsets.UTF_8));
        outputStream.flush();
    }

    private <T> Directive<T> mapDirective(Directive<JsonObject> directive, Class<? extends T> clazz) {
        return Directive.<T>builder()
                .header(directive.getHeader())
                .endpoint(directive.getEndpoint())
                .payload(gson.fromJson(directive.getPayload(), clazz))
                .build();
    }
}
