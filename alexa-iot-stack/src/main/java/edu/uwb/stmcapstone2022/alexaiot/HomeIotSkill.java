package edu.uwb.stmcapstone2022.alexaiot;

import software.amazon.awscdk.services.lambda.Code;
import software.amazon.awscdk.services.lambda.Function;
import software.amazon.awscdk.services.lambda.Runtime;
import software.constructs.Construct;

import java.util.Map;

public class HomeIotSkill extends Construct  {
    public HomeIotSkill(Construct scope, String id) {
        super(scope, id);

        Function handler = Function.Builder.create(this, "HomeIotHandler")
                .runtime(Runtime.JAVA_11)
                .code(Code.fromAsset("../alexa-iot-worker/TODO"))
                .handler("edu.uwb.stmcapstone2022.alexaiot.Handler")
                .build();
    }
}
