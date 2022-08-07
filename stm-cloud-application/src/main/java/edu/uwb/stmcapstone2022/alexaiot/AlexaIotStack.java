package edu.uwb.stmcapstone2022.alexaiot;

import software.amazon.awscdk.Duration;
import software.amazon.awscdk.Stack;
import software.amazon.awscdk.StackProps;
import software.amazon.awscdk.services.iam.*;
import software.amazon.awscdk.services.lambda.Code;
import software.amazon.awscdk.services.lambda.Function;
import software.amazon.awscdk.services.lambda.Runtime;
import software.constructs.Construct;

import java.util.*;

public class AlexaIotStack extends Stack {
    public AlexaIotStack(final Construct scope, final String id, final StackProps props,
                         final Properties properties) {
        super(scope, id, props);

        String thingRegion = properties.getProperty("iot.thing.region");
        String thingName = properties.getProperty("iot.thing.generic.name");
        String sensorName = properties.getProperty("iot.thing.sensor.name");

        String accountId = Objects.requireNonNull(props.getEnv()).getAccount();

        List<PolicyStatement> smartHomeSkillStatements = new ArrayList<>();
        smartHomeSkillStatements.add(PolicyStatement.Builder.create()
                .effect(Effect.ALLOW)
                .resources(List.of(
                        "arn:aws:iot:" + thingRegion + ":" + accountId + ":thing/" + sensorName,
                        "arn:aws:iot:" + thingRegion + ":" + accountId + ":thing/" + thingName))
                .actions(List.of("*"))
                .build());

        smartHomeSkillStatements.add(PolicyStatement.Builder.create()
                .effect(Effect.ALLOW)
                .resources(List.of("*"))
                .actions(List.of(
                        "logs:CreateLogGroup",
                        "logs:CreateLogStream",
                        "logs:PutLogEvents"))
                .build());

        PolicyDocument smartHomeSkillPolicy = PolicyDocument.Builder.create()
                .statements(smartHomeSkillStatements)
                .build();

        Role lambdaRole = Role.Builder.create(this, "EndpointRole")
                .roleName("AlexaSkillEndpointFunctionRole")
                .inlinePolicies(Collections.singletonMap("SmartHomeSkillPolicy", smartHomeSkillPolicy))
                .path("/")
                .assumedBy(new ServicePrincipal("lambda.amazonaws.com"))
                .build();

        Function.Builder.create(this, "AlexaSmartHomeSkillEndpoint")
                .description("Connects to an Alexa Smart Home Skill to control the IoT device")
                .code(Code.fromAsset("alexa-endpoint-worker/target/alexa-endpoint-worker-shaded.jar"))
                .handler("edu.uwb.stmcapstone2022.alexaiot.EndpointHandler")
                .role(lambdaRole)
                .runtime(Runtime.JAVA_11).memorySize(1024)
                .environment(Map.of(
                        "SENSOR_NAME", sensorName,
                        "THING_NAME", thingName,
                        "THING_REGION", thingRegion))
                .timeout(Duration.minutes(5))
                .build();
    }
}
