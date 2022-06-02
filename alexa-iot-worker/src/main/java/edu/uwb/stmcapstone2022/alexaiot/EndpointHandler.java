package edu.uwb.stmcapstone2022.alexaiot;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestStreamHandler;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import edu.uwb.stmcapstone2022.alexaiot.alexa.errors.InvalidDirectiveException;
import edu.uwb.stmcapstone2022.alexaiot.alexa.model.Directive;
import edu.uwb.stmcapstone2022.alexaiot.alexa.model.Event;
import edu.uwb.stmcapstone2022.alexaiot.alexa.model.SkillRequest;
import edu.uwb.stmcapstone2022.alexaiot.alexa.model.SkillResponse;
import edu.uwb.stmcapstone2022.alexaiot.alexa.model.event.AlexaErrorResponse;
import edu.uwb.stmcapstone2022.alexaiot.providers.AlexaAuthorizationProvider;
import edu.uwb.stmcapstone2022.alexaiot.providers.AlexaDiscoveryProvider;
import edu.uwb.stmcapstone2022.alexaiot.providers.AlexaPowerControllerProvider;
import edu.uwb.stmcapstone2022.alexaiot.providers.AlexaProvider;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import lombok.val;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

@Slf4j
public final class EndpointHandler implements RequestStreamHandler {
    private static final DirectiveHandler<Void>
            INVALID_DIRECTIVE_HANDLER = new DirectiveHandler<>(Void.class, directive -> {
                val name = new DirectiveName(directive.getHeader().getNamespace(), directive.getHeader().getName());
        throw new InvalidDirectiveException(name, "Unsupported directive '" + name + "'");
    });
    private final Gson gson = new GsonBuilder().create();
    private final Map<DirectiveName, DirectiveHandler<?>> dispatchTable = new HashMap<>();

    public EndpointHandler() {
        registerProvider(new AlexaDiscoveryProvider());
        registerProvider(new AlexaAuthorizationProvider());
        registerProvider(new AlexaPowerControllerProvider());
        registerProvider(new AlexaProvider());
    }

    private void registerProvider(DirectiveHandlerProvider provider) {
        dispatchTable.putAll(provider.advertiseHandlers());
    }

    private static class RawSkillRequest extends SkillRequest<JsonObject> {
        public RawSkillRequest(@NonNull Directive<JsonObject> directive) {
            super(directive);
        }
    }

    @Override
    public void handleRequest(InputStream inputStream, OutputStream outputStream, Context context) throws IOException {
        Directive<JsonObject> directive = gson.fromJson(new InputStreamReader(inputStream), RawSkillRequest.class)
                .getDirective();
        SkillResponse<?> response;

        try {
            log.info("Directive request: {}", gson.toJson(directive));
            val name = new DirectiveName(directive.getHeader().getNamespace(), directive.getHeader().getName());
            val handler = dispatchTable.getOrDefault(name, INVALID_DIRECTIVE_HANDLER);
            response = invokeHandler(directive, handler);
        } catch (InvalidDirectiveException e) {
            response = SkillResponse.<AlexaErrorResponse>builder()
                    .event(Event.<AlexaErrorResponse>builder()
                            .header(directive.getHeader().toResponseBuilder()
                                    .namespace("Alexa")
                                    .name("ErrorResponse")
                                    .build())
                            .endpoint(directive.getEndpoint())
                            .payload(AlexaErrorResponse.builder()
                                    .type(AlexaErrorResponse.Type.INVALID_DIRECTIVE)
                                    .message(e.getMessage())
                                    .build())
                            .build())
                    .build();
            log.error("Exception: {}", gson.toJson(response));
            outputStream.write(gson.toJson(response).getBytes(StandardCharsets.UTF_8));
            outputStream.flush();
        } catch (RuntimeException e) {
            response = SkillResponse.<AlexaErrorResponse>builder()
                    .event(Event.<AlexaErrorResponse>builder()
                            .header(directive.getHeader().toResponseBuilder()
                                    .namespace("Alexa")
                                    .name("ErrorResponse")
                                    .build())
                            .endpoint(directive.getEndpoint())
                            .payload(AlexaErrorResponse.builder()
                                    .type(AlexaErrorResponse.Type.INTERNAL_ERROR)
                                    .message(e.getMessage())
                                    .build())
                            .build())
                    .build();
            log.error("Exception: {}", gson.toJson(response));
            outputStream.write(gson.toJson(response).getBytes(StandardCharsets.UTF_8));
            outputStream.flush();
        }

        log.info("Skill response: {}", gson.toJson(response));
        outputStream.write(gson.toJson(response).getBytes(StandardCharsets.UTF_8));
        outputStream.flush();
    }

    private <T> SkillResponse<?> invokeHandler(Directive<JsonObject> rawDirective, DirectiveHandler<T> handler) {
        Directive<T> directive = Directive.<T>builder()
                .header(rawDirective.getHeader())
                .endpoint(rawDirective.getEndpoint())
                .payload(gson.fromJson(rawDirective.getPayload(), handler.getPayloadClass()))
                .build();

        return handler.getHandler().apply(directive);
    }
}
