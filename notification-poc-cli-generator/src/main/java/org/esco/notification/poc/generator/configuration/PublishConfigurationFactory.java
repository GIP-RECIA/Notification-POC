package org.esco.notification.poc.generator.configuration;

import org.esco.notification.beans.RandomBean;

public class PublishConfigurationFactory {
    public <T> PublishConfiguration newConfiguration(String configuration) {
        switch (configuration) {
            case "direct":
                return new ExchangeDirectConfiguration();
            case "fanout":
                return new ExchangeFanoutConfiguration();
            case "routing":
                return new ExchangeRoutingConfiguration<RandomBean>((bean) -> {
                    if (bean.getTitle().contains(" ")) {
                        return "long-title";
                    } else {
                        return "short-title";
                    }
                });
            case "topic":
                return new ExchangeTopicConfiguration<RandomBean>((bean) -> {
                    if (bean.getTitle().contains(" ")) {
                        return "random-beans.long-title";
                    } else {
                        return "random-beans.short-title";
                    }
                });
            default:
                throw new IllegalArgumentException("Invalid configuration");
        }
    }
}
