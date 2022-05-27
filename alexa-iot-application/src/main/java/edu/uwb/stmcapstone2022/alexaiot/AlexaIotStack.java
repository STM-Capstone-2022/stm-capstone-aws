package edu.uwb.stmcapstone2022.alexaiot;

import software.amazon.awscdk.Duration;
import software.amazon.awscdk.Stack;
import software.amazon.awscdk.StackProps;
import software.amazon.awscdk.services.iam.*;
import software.amazon.awscdk.services.lambda.Code;
import software.amazon.awscdk.services.lambda.Function;
import software.amazon.awscdk.services.lambda.Runtime;
import software.constructs.Construct;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class AlexaIotStack extends Stack {
    public AlexaIotStack(final Construct scope, final String id) {
        this(scope, id, null);
    }

    public AlexaIotStack(final Construct scope, final String id, final StackProps props) {
        super(scope, id, props);

        String thingNameUnescaped = "ikLkaEbPgpQiP1pL";

        List<PolicyStatement> smartHomeSkillStatements = new ArrayList<>();
        smartHomeSkillStatements.add(PolicyStatement.Builder.create()
                .effect(Effect.ALLOW)
                .resources(List.of("arn:aws:iot:us-west-2:015493416428:thing/" + thingNameUnescaped))
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

        Role lambdaRole = Role.Builder.create(this, "FunctionRole")
                .roleName("FunctionRole")
                .inlinePolicies(Collections.singletonMap("key", smartHomeSkillPolicy))
                .path("/")
                .assumedBy(new ServicePrincipal("lambda.amazonaws.com"))
                .build();

        Function.Builder.create(this, "AlexaSkillFunction")
                .code(Code.fromAsset("alexa-iot-worker/target/alexa-iot-worker-shaded.jar"))
                .handler("edu.uwb.stmcapstone2022.alexaiot.AlexaHandler")
                .role(lambdaRole)
                .runtime(Runtime.JAVA_11).memorySize(1024)
                .environment(Map.of("THING_NAME", thingNameUnescaped))
                .timeout(Duration.minutes(5)).build();
    }
}
