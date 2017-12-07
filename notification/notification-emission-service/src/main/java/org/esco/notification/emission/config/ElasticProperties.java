package org.esco.notification.emission.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "notification.elastic")
public class ElasticProperties {
    private String hostUris;

    public String getHostUris() {
        return hostUris;
    }

    public void setHostUris(String hostUris) {
        this.hostUris = hostUris;
    }
}
