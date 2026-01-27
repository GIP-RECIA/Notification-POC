package org.esco.notification.randombeans.configuration;

public interface RoutingKeyStrategy<T> {
    String getRoutingKey(T bean);
}
