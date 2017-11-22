package org.esco.notification.poc.generator.configuration;

public interface RoutingKeyStrategy<T> {
    String getRoutingKey(T bean);
}
