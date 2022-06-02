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
    private static final String THING_NAME = "ikLkaEbPgpQiP1pL";
    private static final String AWS_ACCOUNTID = "015493416428";
    private static final String BUCKET_NAME = "stm32u5-us-east-1";
    public AlexaIotStack(final Construct scope, final String id) {
        this(scope, id, StackProps.builder()
                .stackName(id)
                .description("Alexa SKill endpoint for managing ST Device Kit")
                .build());
    }

    public AlexaIotStack(final Construct scope, final String id, final StackProps props) {
        super(scope, id, props);

        List<PolicyStatement> smartHomeSkillStatements = new ArrayList<>();
        smartHomeSkillStatements.add(PolicyStatement.Builder.create()
                .effect(Effect.ALLOW)
                .resources(List.of("arn:aws:iot::" + AWS_ACCOUNTID + ":thing/" + THING_NAME))
                .actions(List.of("*"))
                .build());

        smartHomeSkillStatements.add(PolicyStatement.Builder.create()
                .effect(Effect.ALLOW)
                .resources(List.of("arn:aws:s3:::" + BUCKET_NAME + "/*"))
                .actions(List.of("s3:GetObject"))
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

        Function.Builder.create(this, "AlexaSkillEndpoint")
                .code(Code.fromAsset("alexa-iot-worker/target/alexa-iot-worker-shaded.jar"))
                .handler("edu.uwb.stmcapstone2022.alexaiot.EndpointHandler")
                .role(lambdaRole)
                .runtime(Runtime.JAVA_11).memorySize(1024)
                .environment(Map.of(
                        "THING_NAME", THING_NAME,
                        "BUCKET_NAME", BUCKET_NAME))
                .timeout(Duration.minutes(5)).build();
    }
}
