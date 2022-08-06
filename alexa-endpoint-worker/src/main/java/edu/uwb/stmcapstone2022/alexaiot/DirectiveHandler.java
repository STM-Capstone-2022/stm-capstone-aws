package edu.uwb.stmcapstone2022.alexaiot;

import edu.uwb.stmcapstone2022.alexaiot.alexa.model.Directive;
import edu.uwb.stmcapstone2022.alexaiot.alexa.model.SkillResponse;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.function.Function;

@RequiredArgsConstructor
@Getter
public final class DirectiveHandler<T> {
    private final Class<T> payloadClass;
    private final Function<Directive<T>, SkillResponse<?>> handler;
}
