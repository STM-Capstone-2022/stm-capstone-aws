package edu.uwb.stmcapstone2022.alexaiot.alexa.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;
import lombok.experimental.SuperBuilder;

@AllArgsConstructor
@Builder
@Getter
public class SkillRequest<T> {
    @NonNull private final Directive<T> directive;
}
