package edu.uwb.stmcapstone2022.alexaiot;

import com.sun.tools.javac.util.List;
import software.amazon.awscdk.Duration;
import software.amazon.awscdk.Stack;
import software.amazon.awscdk.StackProps;
import software.amazon.awscdk.services.iam.*;
import software.amazon.awscdk.services.lambda.Code;
import software.amazon.awscdk.services.lambda.Function;
import software.amazon.awscdk.services.lambda.Runtime;
import software.constructs.Construct;

import java.util.Collections;

public class AlexaIotStack extends Stack {
    public AlexaIotStack(final Construct scope, final String id) {
        this(scope, id, null);
    }

    public AlexaIotStack(final Construct scope, final String id, final StackProps props) {
        super(scope, id, props);

        // Create policy to allow Shadow update
        PolicyStatement thingStatement = PolicyStatement.Builder.create()
                .effect(Effect.ALLOW)
                .resources(List.of("arn:aws:iot:us-west-2:015493416428:thing/ikLkaEbPgpQiP1pL"))
                .actions(List.of("*"))
                .build();

        PolicyStatement logStatement = PolicyStatement.Builder.create()
                .effect(Effect.ALLOW)
                .resources(List.of("*"))
                .actions(List.of("logs:CreateLogGroup","logs:CreateLogStream","logs:PutLogEvents"))
                .build();

        PolicyDocument policyDocument = PolicyDocument.Builder.create()
                .statements(List.of(thingStatement, logStatement))
                .build();

        Role lambdaRole = Role.Builder.create(this, "LambdaIAMRole")
                .roleName("LambdaIAMRole")
                .inlinePolicies(Collections.singletonMap("key", policyDocument))
                .path("/")
                .assumedBy(new ServicePrincipal("lambda.amazonaws.com"))
                .build();

        Function lambda = Function.Builder.create(this, "HelloLambda")
                .code(Code.fromAsset("../alexa-iot-worker/target/alexa-iot-worker-0.0.0-ALPHA.jar"))
                .handler("edu.uwb.stmcapstone2022.alexaiot.Handler")
                .role(lambdaRole)
                .runtime(Runtime.JAVA_11).memorySize(1024)
                .timeout(Duration.minutes(5)).build();
    }
}
