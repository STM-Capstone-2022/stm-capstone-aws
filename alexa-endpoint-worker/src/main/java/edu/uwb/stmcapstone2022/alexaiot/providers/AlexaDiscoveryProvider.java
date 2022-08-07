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

import java.util.Collections;
import java.util.List;
import java.util.Map;

import static java.util.Collections.singletonList;
import static java.util.Collections.singletonMap;

public final class AlexaDiscoveryProvider implements DirectiveHandlerProvider {
    private SkillResponse<AlexaDiscoveryDiscoverResponse> discover(Directive<AlexaDiscoveryDiscover> request) {
        var payload = AlexaDiscoveryDiscoverResponse.builder()
                .endpoint(AlexaDiscoveryDiscoverResponse.Endpoint.builder()
                        .endpointId("demo-device-01")
                        .manufacturerName("Smart Device Company")
                        .description("Power-controlled device")
                        .friendlyName("Generic Device")
                        .displayCategories(List.of(DisplayCategory.SWITCH))
                        .capability(Capability.builder()
                                .type("AlexaInterface")
                                .interfaceName("Alexa")
                                .version("3")
                                .build())
                        .cookies(Collections.emptyMap())
                        .capability(Capability.builder()
                                .type("AlexaInterface")
                                .interfaceName("Alexa.EndpointHealth")
                                .version("3")
                                .properties(Properties.builder()
                                        .supported(singletonList(singletonMap("name", "connectivity")))
                                        .retrievable(true)
                                        .proactivelyReported(false)
                                        .build())
                                .build())
                        .capability(Capability.builder()
                                .type("AlexaInterface")
                                .interfaceName("Alexa.PowerController")
                                .version("3")
                                .properties(Properties.builder()
                                        .supported(singletonList(singletonMap("name", "powerState")))
                                        .retrievable(true)
                                        .proactivelyReported(false)
                                        .build())
                                .build())
                        .build())
                .endpoint(AlexaDiscoveryDiscoverResponse.Endpoint.builder()
                        .endpointId("door-sensor-01")
                        .manufacturerName("Smart Device Company")
                        .description("Smart contact sensor")
                        .friendlyName("Door Knock Sensor")
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
                                        .supported(singletonList(singletonMap("name", "connectivity")))
                                        .retrievable(true)
                                        .proactivelyReported(false)
                                        .build())
                                .build())
                        .capability(Capability.builder()
                                .type("AlexaInterface")
                                .interfaceName("Alexa.MotionSensor")
                                .version("3")
                                .properties(Properties.builder()
                                        .supported(singletonList(singletonMap("name", "detectionState")))
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