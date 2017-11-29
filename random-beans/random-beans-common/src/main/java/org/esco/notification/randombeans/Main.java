package org.esco.notification.randombeans;

import io.airlift.airline.Help;
import io.airlift.airline.ParseException;
import io.airlift.airline.SingleCommand;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public interface Main {
    Logger log = LoggerFactory.getLogger(Main.class.getName());

    static <C extends AbstractCli> C singleCommand(Class<C> command, String[] args) {
        SingleCommand<C> cliParser = SingleCommand.singleCommand(command);

        C tmpCli = null;
        try {
            tmpCli = cliParser.parse(args);
        } catch (ParseException e) {
            System.out.println(e.getLocalizedMessage());
            Help.help(cliParser.getCommandMetadata());
            System.exit(1);
        }
        final C cli = tmpCli;
        if (cli.helpOption.showHelpIfRequested()) {
            System.exit(0);
        }
        cli.verboseOption.enableIfRequested();

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            try {
                cli.close();
            } catch (IOException e) {
                log.warn("An error has occured while closing CLI", e);
            }
        }));

        return cli;
    }
}
