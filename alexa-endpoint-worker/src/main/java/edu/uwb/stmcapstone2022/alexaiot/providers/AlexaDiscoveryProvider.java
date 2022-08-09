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
import java.util.Map;

import static java.util.Collections.singletonList;
import static java.util.Collections.singletonMap;

public final class AlexaDiscoveryProvider implements DirectiveHandlerProvider {
    private static AlexaDiscoveryDiscoverResponse.Endpoint.Builder standardDevice() {
        return AlexaDiscoveryDiscoverResponse.Endpoint.builder()
                .manufacturerName("Smart Capstone Ltd.")
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
                        .build());
    }

    private static AlexaDiscoveryDiscoverResponse.Capability powerCapability() {
        return Capability.builder()
                .type("AlexaInterface")
                .interfaceName("Alexa.PowerController")
                .version("3")
                .properties(Properties.builder()
                        .supported(singletonList(singletonMap("name", "powerState")))
                        .retrievable(true)
                        .proactivelyReported(false)
                        .build())
                .build();
    }

    private SkillResponse<AlexaDiscoveryDiscoverResponse> discover(Directive<AlexaDiscoveryDiscover> request) {
        // Smart Light
        // RGB light
        // Fan
        // Humidifier
        // Stupid Door Lock
        var payload = AlexaDiscoveryDiscoverResponse.builder()
                .endpoint(standardDevice()
                        .endpointId("demo-lock-01")
                        .description("Door Lock")
                        .friendlyName("Front Door Lock")
                        .displayCategories(singletonList(DisplayCategory.SMARTLOCK))
                        .capability(powerCapability())
                        .build())
                .endpoint(standardDevice()
                        .endpointId("demo-light-01")
                        .description("RGB Light")
                        .friendlyName("Front Door Light")
                        .displayCategories(singletonList(DisplayCategory.LIGHT))
                        .capability(powerCapability())
                        .build())
                .endpoint(standardDevice()
                        .endpointId("demo-fan-01")
                        .description("Rotary Fan")
                        .friendlyName("Living Room Fan")
                        .displayCategories(singletonList(DisplayCategory.FAN))
                        .capability(powerCapability())
                        .build())
                .endpoint(standardDevice()
                        .endpointId("demo-humidifier-01")
                        .description("Swamp Humidifier")
                        .friendlyName("Humidifier")
                        .displayCategories(singletonList(DisplayCategory.OTHER))
                        .capability(powerCapability())
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