package org.esco.notification.randombeans;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import io.airlift.airline.Command;
import io.airlift.airline.Option;
import org.esco.notification.randombeans.configuration.ConsumeConfiguration;
import org.esco.notification.randombeans.configuration.ConsumeConfigurationFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Closeable;
import java.io.IOException;
import java.util.concurrent.TimeoutException;

@Command(name = "random-beans-consumer", description = "Consumes random generated beans")
public class Cli extends AbstractCli implements Closeable {
    static Logger log = LoggerFactory.getLogger(Cli.class.getName());

    private ConnectionFactory connectionFactory;
    private Connection connection;
    private ConsumeConfiguration consumeConfiguration;

    public static void main(String[] args) throws IOException, TimeoutException {
        Cli cli = Main.singleCommand(Cli.class, args);
        cli.connect();

        cli.listen();

        Object forever = new Object();
        synchronized (forever) {
            try {
                forever.wait();
            } catch (InterruptedException e) {
            }
        }
    }

    @Option(name = {"-c", "--configuration"}, description = "Consume configuration")
    public String configuration = "topic";

    public void connect() throws IOException, TimeoutException {
        this.connectionFactory = this.connectionFactoryOptions.buildConnectionFactory();
        this.connection = this.connectionFactory.newConnection("cli-consumer");
        this.consumeConfiguration = new ConsumeConfigurationFactory().newConfiguration(this.configuration);
    }

    public void close() throws IOException {
        this.connection.close();
    }


    public void listen() throws IOException {
        Channel channel = connection.createChannel();
        ObjectMapper objectMapper = new ObjectMapper(new JsonFactory());
        objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);

        consumeConfiguration.configure(channel, objectMapper, log);
    }

}
