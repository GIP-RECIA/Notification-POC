package org.esco.notification.poc.common.options;

import io.airlift.airline.Option;
import io.airlift.airline.model.CommandMetadata;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;

public class VerboseOption {
    @Inject
    public CommandMetadata commandMetadata;

    @Option(name = {"-v", "--verbose"}, description = "Verbose")
    public Boolean verbose = false;

    public void enableIfRequested() {
        if (verbose) {
            ch.qos.logback.classic.Logger root = (ch.qos.logback.classic.Logger) LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME);
            root.setLevel(ch.qos.logback.classic.Level.TRACE);
        }
    }
}