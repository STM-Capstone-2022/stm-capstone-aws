package edu.uwb.stmcapstone2022.alexaiot;

import java.util.Map;

public interface DirectiveHandlerProvider {
    Map<DirectiveName, DirectiveHandler<?>> advertiseHandlers();
}
