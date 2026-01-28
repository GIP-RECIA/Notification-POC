package fr.recia.model_kafka_poc.model;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.common.header.Headers;
import org.apache.kafka.common.header.internals.RecordHeaders;
import org.apache.kafka.common.serialization.Deserializer;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serializer;

import java.nio.charset.StandardCharsets;
import java.util.Map;

public class RoutedNotificationSerde implements Serde<RoutedNotification> {

    private final ObjectMapper objectMapper;

    public RoutedNotificationSerde(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public Serializer<RoutedNotification> serializer() {
        return new Serializer<>() {

            @Override
            public byte[] serialize(String topic, Headers headers, RoutedNotification data) {
                try {
                    headers.add(
                            "__TypeId__",
                            RoutedNotification.class.getName().getBytes(StandardCharsets.UTF_8)
                    );
                    return objectMapper.writeValueAsBytes(data);
                } catch (Exception e) {
                    throw new RuntimeException("RoutedNotification serialization error", e);
                }
            }

            @Override
            public byte[] serialize(String topic, RoutedNotification data) {
                return serialize(topic, new RecordHeaders(), data);
            }
        };
    }

    @Override
    public Deserializer<RoutedNotification> deserializer() {
        return (topic, data) -> {
            try {
                return objectMapper.readValue(data, RoutedNotification.class);
            } catch (Exception e) {
                throw new RuntimeException("RoutedNotification deserialization error", e);
            }
        };
    }

    @Override
    public void configure(Map<String, ?> configs, boolean isKey) {}

    @Override
    public void close() {}
}

