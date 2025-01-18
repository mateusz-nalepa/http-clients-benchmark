package com.mateusznalepa.http.clients.util.logger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CustomLoggerWrapper {

    private static final Logger CustomLogger = LoggerFactory.getLogger("Custom-Logger");


    public static void log(String message) {
        CustomLogger.debug("CUSTOM_LOGGER: {}", message);
    }

}
