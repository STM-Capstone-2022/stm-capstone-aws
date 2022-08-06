package edu.uwb.stmcapstone2022.alexaiot.providers;

import edu.uwb.stmcapstone2022.alexaiot.DirectiveHandler;
import edu.uwb.stmcapstone2022.alexaiot.DirectiveHandlerProvider;
import edu.uwb.stmcapstone2022.alexaiot.DirectiveName;
import edu.uwb.stmcapstone2022.alexaiot.alexa.model.Directive;
import edu.uwb.stmcapstone2022.alexaiot.alexa.model.Event;
import edu.uwb.stmcapstone2022.alexaiot.alexa.model.SkillResponse;
import edu.uwb.stmcapstone2022.alexaiot.alexa.model.directive.AlexaDiscoveryDiscover;
import edu.uwb.stmcapstone2022.alexaiot.alexa.model.event.AlexaDiscoveryDiscoverResponse;
import edu.uwb.stmcapstone2022.alexaiot.alexa.model.event.AlexaDiscoveryDiscoverResponse.Capability;
import edu.uwb.stmcapstone2022.alexaiot.alexa.model.event.AlexaDiscoveryDiscoverResponse.DisplayCategory;
import edu.uwb.stmcapstone2022.alexaiot.alexa.model.event.AlexaDiscoveryDiscoverResponse.Properties;
import lombok.var;

import java.util.List;
import java.util.Map;

public final class AlexaDiscoveryProvider implements DirectiveHandlerProvider {
    private SkillResponse<AlexaDiscoveryDiscoverResponse> discover(Directive<AlexaDiscoveryDiscover> request) {
        var payload = AlexaDiscoveryDiscoverResponse.builder()
                .endpoint(AlexaDiscoveryDiscoverResponse.Endpoint.builder()
                        .endpointId("demo-device-01")
                        .manufacturerName("Smart Device Company")
                        .friendlyName("Generic Device")
                        .description("Power-controlled device")
                        .displayCategories(List.of(DisplayCategory.SWITCH))
                        .capability(Capability.builder()
                                .type("AlexaInterface")
                                .interfaceName("Alexa")
                                .version("3")
                                .build())
                        .capability(Capability.builder()
                                .type("AlexaInterface")
                                .interfaceName("Alexa.EndpointHealth")
                                .version("3")
                                .properties(Properties.builder()
                                        .supported(List.of(
                                                Map.of("name", "connectivity")))
                                        .retrievable(true)
                                        .proactivelyReported(false)
                                        .build())
                                .build())
                        .capability(Capability.builder()
                                .type("AlexaInterface")
                                .interfaceName("Alexa.PowerController")
                                .version("3")
                                .properties(Properties.builder()
                                        .supported(List.of(
                                                Map.of("name", "powerState")))
                                        .retrievable(true)
                                        .proactivelyReported(false)
                                        .build())
                                .build())
                        .build())
                .endpoint(AlexaDiscoveryDiscoverResponse.Endpoint.builder()
                        .endpointId("door-sensor-01")
                        .manufacturerName("Smart Device Company")
                        .friendlyName("Door Knock Sensor")
                        .description("Smart contact sensor")
                        .displayCategories(List.of(DisplayCategory.DOOR, DisplayCategory.DOORBELL))
                        .capability(Capability.builder()
                                .type("AlexaInterface")
                                .interfaceName("Alexa")
                                .version("3")
                                .build())
                        .capability(Capability.builder()
                                .type("AlexaInterface")
                                .interfaceName("Alexa.EndpointHealth")
                                .version("3")
                                .properties(Properties.builder()
                                        .supported(List.of(
                                                Map.of("name", "connectivity")))
                                        .retrievable(true)
                                        .proactivelyReported(false)
                                        .build())
                                .build())
                        .capability(Capability.builder()
                                .type("AlexaInterface")
                                .interfaceName("Alexa.MotionSensor")
                                .version("3")
                                .properties(Properties.builder()
                                        .supported(List.of(
                                                Map.of("name", "detectionState")))
                                        .retrievable(true)
                                        .proactivelyReported(false)
                                        .build())
                                .build())
                        .build())
                .build();

        return SkillResponse.<AlexaDiscoveryDiscoverResponse>builder()
                .event(Event.<AlexaDiscoveryDiscoverResponse>builder()
                        .header(request.getHeader().toResponseBuilder()
                                .name("Discover.Response")
                                .build())
                        .payload(payload)
                        .build())
                .build();
    }

    @Override
    public Map<DirectiveName, DirectiveHandler<?>> advertiseHandlers() {
        return Map.of(new DirectiveName("Alexa.Discovery", "Discover"),
                new DirectiveHandler<>(AlexaDiscoveryDiscover.class, this::discover));
    }
}