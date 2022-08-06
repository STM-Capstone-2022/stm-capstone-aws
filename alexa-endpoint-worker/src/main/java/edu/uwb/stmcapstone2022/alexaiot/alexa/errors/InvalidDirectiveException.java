package edu.uwb.stmcapstone2022.alexaiot.alexa.errors;

import edu.uwb.stmcapstone2022.alexaiot.DirectiveName;
import lombok.Getter;

@Getter
public class InvalidDirectiveException extends RuntimeException {
    private final DirectiveName directiveName;

    public InvalidDirectiveException(DirectiveName directiveName, String message) {
        super(message);
        this.directiveName = directiveName;
    }
}
