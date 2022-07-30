package edu.uwb.stmcapstone2022.alexaiot;

import edu.uwb.stmcapstone2022.alexaiot.alexa.model.Directive;
import lombok.Data;
import lombok.NonNull;

@Data
public final class DirectiveName {
    @NonNull private String namespace;
    @NonNull private String name;

    public static DirectiveName fromDirective(Directive<?> directive) {
        return new DirectiveName(directive.getHeader().getNamespace(), directive.getHeader().getName());
    }

    @Override
    public String toString() {
        return namespace + "::" + name;
    }
}