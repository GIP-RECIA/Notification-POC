package org.esco.notification.randombeans;

import io.airlift.airline.Help;
import io.airlift.airline.Option;
import io.airlift.airline.model.CommandMetadata;

import javax.inject.Inject;

public class LongHelpOption {
    @Inject
    public CommandMetadata commandMetadata;

    @Option(name = {"--help"}, description = "Display help information")
    public Boolean help = false;

    public boolean showHelpIfRequested() {
        if (help) {
            Help.help(commandMetadata);
        }
        return help;
    }
}