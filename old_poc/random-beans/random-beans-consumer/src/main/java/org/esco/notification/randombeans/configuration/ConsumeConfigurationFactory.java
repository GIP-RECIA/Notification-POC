package org.esco.notification.randombeans.configuration;

public class ConsumeConfigurationFactory {
    public ConsumeConfiguration newConfiguration(String configuration) {
        switch (configuration) {
            case "direct":
                return new ExchangeDirectConfiguration();
            case "fanout":
                return new ExchangeFanoutConfiguration();
            case "routing":
                return new ExchangeRoutingConfiguration();
            case "topic":
                return new ExchangeTopicConfiguration();
            default:
                throw new IllegalArgumentException("Invalid configuration");
        }
    }
}
