package edu.uwb.stmcapstone2022.alexaiot.alexa.model;

import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;

@Builder
@Getter
public class SkillResponse<T> {
    @NonNull private final Event<T> event;
    private final Context context;
}
