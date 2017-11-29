package org.esco.notification.randombeans;

import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import io.airlift.airline.Command;
import io.airlift.airline.Option;
import org.esco.notification.randombeans.configuration.PublishConfiguration;
import org.esco.notification.randombeans.configuration.PublishConfigurationFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Random;
import java.util.concurrent.TimeoutException;

@Command(name = "random-beans-generator", description = "Generates random beans events and send them to RabbitMQ")
public class Cli extends AbstractCli {
    static Logger log = LoggerFactory.getLogger(Cli.class.getName());

    @Option(name = {"-w", "--wait"}, description = "Wait between event generation")
    public int wait = 250;

    @Option(name = {"-c", "--configuration"}, description = "Publish configuration")
    public String configuration = "topic";

    Collection<Generator> generators = new ArrayList<>();

    private ConnectionFactory connectionFactory;
    private Connection connection;
    private PublishConfiguration publishConfiguration;

    public static void main(String[] args) throws IOException, TimeoutException {
        Cli cli = Main.singleCommand(Cli.class, args);

        cli.connect();
        cli.addRandomBeansGenerator();
        cli.loop();
    }


    public void connect() throws IOException, TimeoutException {
        this.connectionFactory = connectionFactoryOptions.buildConnectionFactory();
        this.connection = this.connectionFactory.newConnection("cli-generator");
        this.publishConfiguration = new PublishConfigurationFactory().newConfiguration(this.configuration);
    }

    public void close() throws IOException {
        if (this.connection != null) {
            this.connection.close();
        }
    }

    public void addRandomBeansGenerator() throws IOException, TimeoutException {
        generators.add(new RandomBeanEventsGenerator(connection, this.publishConfiguration));
    }

    public void loop() {
        while (true) {
            for (Generator generator : generators) {
                try {
                    generator.generate();
                } catch (GeneratorException e) {
                    log.error("An error has occured while generating data.", e);
                }

                try {
                    double waitVariant = 0.8 + new Random().nextDouble() * 0.4;
                    Thread.sleep((long) (wait * waitVariant));
                } catch (InterruptedException e) {
                    return;
                }
            }
        }

    }
}
