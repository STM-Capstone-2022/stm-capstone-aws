package edu.uwb.stmcapstone2022.alexaiot;

import software.amazon.awscdk.App;
import software.amazon.awscdk.StackProps;

public class AlexaIotApp {
    public static void main(final String[] args) {
        App app = new App();

        new AlexaIotStack(app, "alexa-iot");

        app.synth();
    }
}

