package org.esco.notification.poc.common.options;

import com.rabbitmq.client.ConnectionFactory;
import io.airlift.airline.Option;
import io.airlift.airline.model.CommandMetadata;

import javax.inject.Inject;

public class ConnectionFactoryOptions {
    @Inject
    public CommandMetadata commandMetadata;

    @Option(name = {"-h", "--host"}, description = "RabbitMQ Host")
    public String host = "localhost";

    @Option(name = {"-p", "--port"}, description = "RabbitMQ Port")
    public Integer port = null;

    @Option(name = {"-u", "--username"}, description = "RabbitMQ Username")
    public String username = "admin";

    @Option(name = {"-P", "--password"}, description = "RabbitMQ Password")
    public String password = "admin";

    public ConnectionFactory buildConnectionFactory() {
        ConnectionFactory connectionFactory = new ConnectionFactory();
        if (this.host != null) {
            connectionFactory.setHost(this.host);
        }
        if (this.port != null) {
            connectionFactory.setPort(this.port);
        }
        if (this.username != null) {
            connectionFactory.setUsername(this.username);
        }
        if (this.password != null) {
            connectionFactory.setPassword(this.password);
        }
        return connectionFactory;
    }
}