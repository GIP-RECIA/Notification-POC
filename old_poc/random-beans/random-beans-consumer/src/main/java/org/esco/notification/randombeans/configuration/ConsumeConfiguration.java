package org.esco.notification.randombeans.configuration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rabbitmq.client.Channel;
import org.slf4j.Logger;

import java.io.IOException;

public interface ConsumeConfiguration {
    void configure(Channel channel, ObjectMapper objectMapper, Logger log) throws IOException;
}
