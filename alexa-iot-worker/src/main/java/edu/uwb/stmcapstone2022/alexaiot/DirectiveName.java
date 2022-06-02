package edu.uwb.stmcapstone2022.alexaiot;

import lombok.Data;
import lombok.NonNull;

@Data
public final class DirectiveName {
    @NonNull private String namespace;
    @NonNull private String name;

    @Override
    public String toString() {
        return namespace + "::" + name;
    }
}