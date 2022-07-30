package edu.uwb.stmcapstone2022.alexaiot;

import software.amazon.awscdk.Duration;
import software.amazon.awscdk.Environment;
import software.amazon.awscdk.Stack;
import software.amazon.awscdk.StackProps;
import software.amazon.awscdk.services.iam.*;
import software.amazon.awscdk.services.lambda.Code;
import software.amazon.awscdk.services.lambda.Function;
import software.amazon.awscdk.services.lambda.Runtime;
import software.constructs.Construct;

import java.util.*;

public class AlexaIotStack extends Stack {
    private static final String SENSOR_NAME = "abcxyz";
    private static final String THING_NAME = "ikLkaEbPgpQiP1pL";
    private static final String THING_REGION = "us-west-2";

    public AlexaIotStack(final Construct scope, final String id) {
        this(scope, id, StackProps.builder()
                .stackName(id)
                .description("Alexa Skill endpoint for managing ST Device Kit")
                .env(Environment.builder()
                        .account(System.getenv("CDK_DEFAULT_ACCOUNT"))
                        .region(System.getenv("CDK_DEFAULT_REGION"))
                        .build())
                .build());
    }

    public AlexaIotStack(final Construct scope, final String id, final StackProps props) {
        super(scope, id, props);

        String accountId = Objects.requireNonNull(props.getEnv()).getAccount();

        List<PolicyStatement> smartHomeSkillStatements = new ArrayList<>();
        smartHomeSkillStatements.add(PolicyStatement.Builder.create()
                .effect(Effect.ALLOW)
                .resources(List.of(
                        "arn:aws:iot:" + THING_REGION + ":" + accountId + ":thing/" + SENSOR_NAME,
                        "arn:aws:iot:" + THING_REGION + ":" + accountId + ":thing/" + THING_NAME))
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
                .code(Code.fromAsset("alexa-iot-worker/target/alexa-iot-worker-shaded.jar"))
                .handler("edu.uwb.stmcapstone2022.alexaiot.EndpointHandler")
                .role(lambdaRole)
                .runtime(Runtime.JAVA_11).memorySize(1024)
                .environment(Map.of(
                        "SENSOR_NAME", SENSOR_NAME,
                        "THING_NAME", THING_NAME,
                        "THING_REGION", THING_REGION))
                .timeout(Duration.minutes(5))
                .build();
    }
}
