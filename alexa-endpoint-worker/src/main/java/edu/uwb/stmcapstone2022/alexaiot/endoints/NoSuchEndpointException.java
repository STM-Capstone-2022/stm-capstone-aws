package edu.uwb.stmcapstone2022.alexaiot.endoints;

public class NoSuchEndpointException extends RuntimeException {
    public NoSuchEndpointException(String message) {
        super(message);
    }
}
