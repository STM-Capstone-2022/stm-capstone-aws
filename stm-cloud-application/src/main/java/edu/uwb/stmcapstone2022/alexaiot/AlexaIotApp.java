package edu.uwb.stmcapstone2022.alexaiot;

import software.amazon.awscdk.App;
import software.amazon.awscdk.Environment;
import software.amazon.awscdk.StackProps;

import java.util.Properties;

public class AlexaIotApp {
    public static void main(final String[] args) {
        Properties properties = ApplicationProperties.getProperties();

        App app = new App();
        Environment env = Environment.builder()
                .account(System.getenv("CDK_DEFAULT_ACCOUNT"))
                // The region *must* be us-east-1, to match the Alexa default Endpoint's fixed region.
                .region("us-east-1")
                .build();

        new AlexaIotStack(app, "stm-alexa-endpoint",
                StackProps.builder()
                        .stackName("stm-alexa-endpoint")
                        .description("Alexa Skill Endpoint for managing ST Device Kit")
                        .env(env)
                        .build(),
                properties);

        app.synth();
    }
}

