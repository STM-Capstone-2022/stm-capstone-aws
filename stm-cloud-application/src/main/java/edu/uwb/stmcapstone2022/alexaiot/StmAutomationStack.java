package edu.uwb.stmcapstone2022.alexaiot;

import software.amazon.awscdk.Stack;
import software.amazon.awscdk.StackProps;
import software.amazon.awscdk.services.iam.*;
import software.amazon.awscdk.services.iotevents.CfnDetectorModel;
import software.amazon.awscdk.services.iotevents.CfnInput;
import software.constructs.Construct;

import java.util.*;

import static java.util.Collections.emptyList;

public class StmAutomationStack extends Stack {
    public StmAutomationStack(final Construct scope, final String id, final StackProps props,
                              final Properties properties) {
        super(scope, id, props);

        String thingRegion = properties.getProperty("iot.thing.region");
        String relayName = properties.getProperty("iot.thing.relay.name");
        String accountId = Objects.requireNonNull(props.getEnv()).getAccount();

        List<PolicyStatement> policyStatements = new ArrayList<>();
        policyStatements.add(PolicyStatement.Builder.create()
                .effect(Effect.ALLOW)
                .resources(List.of("arn:aws:iot:" + thingRegion + ":" + accountId + ":topic/*"))
                .actions(List.of("iot:Publish"))
                .build());

        policyStatements.add(PolicyStatement.Builder.create()
                .effect(Effect.ALLOW)
                .resources(List.of("*"))
                .actions(List.of(
                        "logs:CreateLogGroup",
                        "logs:CreateLogStream",
                        "logs:PutLogEvents"))
                .build());

        PolicyDocument policy = PolicyDocument.Builder.create()
                .statements(policyStatements)
                .build();

        Role detectorRole = Role.Builder.create(this, "DetectorRole")
                .roleName("task-detector-stm-automation")
                .inlinePolicies(Collections.singletonMap("DetectorPolicy", policy))
                .path("/")
                .assumedBy(new ServicePrincipal("iotevents.amazonaws.com"))
                .build();

        CfnInput input = CfnInput.Builder.create(this, "stmSensorInput")
                .inputName("stmSensorInput")
                .inputDescription("Event payload of the STM Device Sensor")
                .inputDefinition(CfnInput.InputDefinitionProperty.builder()
                        .attributes(List.of(
                                Map.of("jsonPath", "humidity"),
                                Map.of("jsonPath", "temp")))
                        .build())
                .build();

        String inputName = input.getInputName();
        String isTmpLtThresholdExpression = "$input." + inputName + ".temp < 40";
        String isTmpGtThresholdExpression = "$input." + inputName + ".temp > 40";
        String isHumLtThresholdExpression = "$input." + inputName + ".humidity < 37";
        String isHumGtThresholdExpression = "$input." + inputName + ".humidity > 37";

        String updateRelayTopic = "$aws/things/" + relayName + "/shadow/update";

        String turnOffFanExpression = "'{\"state\":{\"desired\":{\"RELAY_4_On\": \"0\"}}}'";
        String turnOnFanExpression = "'{\"state\":{\"desired\":{\"RELAY_4_On\": \"1\"}}}'";
        String turnOffHumidifierExpression = "'{\"state\":{\"desired\":{\"RELAY_2_On\": \"0\"}}}'";
        String turnOnHumidifierExpression = "'{\"state\":{\"desired\":{\"RELAY_2_On\": \"1\"}}}'";

        CfnDetectorModel.Builder.create(this, "FanAutomationDetector")
                .detectorModelName("stm-fan-automation-detector")
                .detectorModelDefinition(CfnDetectorModel.DetectorModelDefinitionProperty.builder()
                        .initialStateName("Setup")
                        .states(List.of(
                                Map.of(
                                        "stateName", "Setup",
                                        "onInput", Map.of(
                                                "events", emptyList(),
                                                "transitionEvents", List.of(
                                                        Map.of(
                                                                "eventName", "IsCold",
                                                                "condition", isTmpLtThresholdExpression,
                                                                "actions", emptyList(),
                                                                "nextState", "FanOff"),
                                                        Map.of(
                                                                "eventName", "IsWarm",
                                                                "condition", isTmpGtThresholdExpression,
                                                                "actions", emptyList(),
                                                                "nextState", "FanOn"))),
                                        "onEnter", Map.of("events", emptyList()),
                                        "onExit", Map.of("events", emptyList())),
                                Map.of(
                                        "stateName", "FanOff",
                                        "onInput", Map.of(
                                                "events", emptyList(),
                                                "transitionEvents", List.of(
                                                        Map.of(
                                                                "eventName", "WarmedUp",
                                                                "condition", isTmpGtThresholdExpression,
                                                                "actions", emptyList(),
                                                                "nextState", "FanOn"))),
                                        "onEnter", Map.of(
                                                "events", List.of(
                                                        Map.of(
                                                                "eventName", "TurnOffFan",
                                                                "condition", "true",
                                                                "actions", List.of(
                                                                        Map.of("iotTopicPublish",
                                                                                Map.of("mqttTopic", updateRelayTopic,
                                                                                        "payload", Map.of(
                                                                                                "contentExpression", turnOffFanExpression,
                                                                                                "type", "JSON"))))))),
                                        "onExit", Map.of("events", emptyList())),
                                Map.of(
                                        "stateName", "FanOn",
                                        "onInput", Map.of(
                                                "events", emptyList(),
                                                "transitionEvents", List.of(
                                                        Map.of(
                                                                "eventName", "CooledDown",
                                                                "condition", isTmpLtThresholdExpression,
                                                                "actions", emptyList(),
                                                                "nextState", "FanOff"))),
                                        "onEnter", Map.of(
                                                "events", List.of(
                                                        Map.of(
                                                                "eventName", "TurnOnFan",
                                                                "condition", "true",
                                                                "actions", List.of(
                                                                        Map.of("iotTopicPublish",
                                                                                Map.of("mqttTopic", updateRelayTopic,
                                                                                        "payload", Map.of(
                                                                                                "contentExpression", turnOnFanExpression,
                                                                                                "type", "JSON"))))))),
                                        "onExit", Map.of("events", emptyList()))))
                        .build())
                .evaluationMethod("BATCH")
                .roleArn(detectorRole.getRoleArn())
                .build();

        CfnDetectorModel.Builder.create(this, "HumidifierAutomationDetector")
                .detectorModelName("stm-humidifier-automation-detector")
                .detectorModelDefinition(CfnDetectorModel.DetectorModelDefinitionProperty.builder()
                        .initialStateName("Setup")
                        .states(List.of(
                                Map.of(
                                        "stateName", "Setup",
                                        "onInput", Map.of(
                                                "events", emptyList(),
                                                "transitionEvents", List.of(
                                                        Map.of(
                                                                "eventName", "IsDry",
                                                                "condition", isHumLtThresholdExpression,
                                                                "actions", emptyList(),
                                                                "nextState", "HumidifierOn"),
                                                        Map.of(
                                                                "eventName", "IsHumid",
                                                                "condition", isHumGtThresholdExpression,
                                                                "actions", emptyList(),
                                                                "nextState", "HumidifierOff"))),
                                        "onEnter", Map.of("events", emptyList()),
                                        "onExit", Map.of("events", emptyList())),
                                Map.of(
                                        "stateName", "HumidifierOff",
                                        "onInput", Map.of(
                                                "events", emptyList(),
                                                "transitionEvents", List.of(
                                                        Map.of(
                                                                "eventName", "DriedUp",
                                                                "condition", isHumLtThresholdExpression,
                                                                "actions", emptyList(),
                                                                "nextState", "HumidifierOn"))),
                                        "onEnter", Map.of(
                                                "events", List.of(
                                                        Map.of(
                                                                "eventName", "TurnOffHumidifier",
                                                                "condition", "true",
                                                                "actions", List.of(
                                                                        Map.of("iotTopicPublish",
                                                                                Map.of("mqttTopic", updateRelayTopic,
                                                                                        "payload", Map.of(
                                                                                                "contentExpression", turnOffHumidifierExpression,
                                                                                                "type", "JSON"))))))),
                                        "onExit", Map.of("events", emptyList())),
                                Map.of(
                                        "stateName", "HumidifierOn",
                                        "onInput", Map.of(
                                                "events", emptyList(),
                                                "transitionEvents", List.of(
                                                        Map.of(
                                                                "eventName", "MoistedUp", // ??? Naming?
                                                                "condition", isHumGtThresholdExpression,
                                                                "actions", emptyList(),
                                                                "nextState", "HumidifierOff"))),
                                        "onEnter", Map.of(
                                                "events", List.of(
                                                        Map.of(
                                                                "eventName", "TurnOnHumidifier",
                                                                "condition", "true",
                                                                "actions", List.of(
                                                                        Map.of("iotTopicPublish",
                                                                                Map.of("mqttTopic", updateRelayTopic,
                                                                                        "payload", Map.of(
                                                                                                "contentExpression", turnOnHumidifierExpression,
                                                                                                "type", "JSON"))))))),
                                        "onExit", Map.of("events", emptyList()))))
                        .build())
                .evaluationMethod("BATCH")
                .roleArn(detectorRole.getRoleArn())
                .build();
    }
}
