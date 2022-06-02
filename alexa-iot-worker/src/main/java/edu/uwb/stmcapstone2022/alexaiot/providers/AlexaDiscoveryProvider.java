package edu.uwb.stmcapstone2022.alexaiot.providers;

import edu.uwb.stmcapstone2022.alexaiot.DirectiveHandler;
import edu.uwb.stmcapstone2022.alexaiot.DirectiveHandlerProvider;
import edu.uwb.stmcapstone2022.alexaiot.DirectiveName;
import edu.uwb.stmcapstone2022.alexaiot.alexa.model.Directive;
import edu.uwb.stmcapstone2022.alexaiot.alexa.model.Event;
import edu.uwb.stmcapstone2022.alexaiot.alexa.model.SkillResponse;
import edu.uwb.stmcapstone2022.alexaiot.alexa.model.directive.AlexaDiscoveryDiscover;
import edu.uwb.stmcapstone2022.alexaiot.alexa.model.event.AlexaDiscoveryDiscoverResponse;
import edu.uwb.stmcapstone2022.alexaiot.alexa.model.event.AlexaDiscoveryDiscoverResponse.AdditionalAttributes;
import edu.uwb.stmcapstone2022.alexaiot.alexa.model.event.AlexaDiscoveryDiscoverResponse.Capability;
import edu.uwb.stmcapstone2022.alexaiot.alexa.model.event.AlexaDiscoveryDiscoverResponse.DisplayCategory;
import lombok.var;

import java.util.List;
import java.util.Map;

public final class AlexaDiscoveryProvider implements DirectiveHandlerProvider {
    private SkillResponse<AlexaDiscoveryDiscoverResponse> discover(Directive<AlexaDiscoveryDiscover> request) {
        var payload = AlexaDiscoveryDiscoverResponse.builder()
                .endpoint(AlexaDiscoveryDiscoverResponse.Endpoint.builder()
                        .endpointId("sample-bulb-01")
                        .manufacturerName("Smart Device Company")
                        .friendlyName("Livingroom Lamp")
                        .description("Virtual smart light bulb")
                        .displayCategories(List.of(DisplayCategory.LIGHT))
                        .additionalAttributes(AdditionalAttributes.builder()
                                .manufacturer("Sample Manufacturer")
                                .model("Sample Model")
                                .serialNumber("U11112233456")
                                .firmwareVersion("1.24.2546")
                                .softwareVersion("1.036")
                                .customIdentifier("Sample custom ID")
                                .build())
                        .cookies(Map.of(
                                "key1", "Arbitrary key/value pairs for skill to reference this endpoint",
                                "key2", "There can be multiple entries",
                                "key3", "but they should only be used for reference purposes",
                                "key4", "this is not a suitable place for endpoint state."))
                        .capability(Capability.builder()
                                .type("AlexaInterface")
                                .interfaceName("Alexa")
                                .version("3")
                                .build())
                        .capability(Capability.builder()
                                .type("AlexaInterface")
                                .interfaceName("Alexa.PowerController")
                                .version("3")
                                .properties(AlexaDiscoveryDiscoverResponse.Properties.builder()
                                        .supported(List.of(
                                                Map.of("name", "powerState")))
                                        .retrievable(true)
                                        .build())
                                .build())
                        .capability(Capability.builder()
                                .type("AlexaInterface")
                                .interfaceName("Alexa.EndpointHealth")
                                .version("3.2")
                                .properties(AlexaDiscoveryDiscoverResponse.Properties.builder()
                                        .supported(List.of(
                                                Map.of("name", "connectivity")))
                                        .retrievable(true)
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