package fr.recia.model_kafka_poc.model;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.common.header.Headers;
import org.apache.kafka.common.header.internals.RecordHeaders;
import org.apache.kafka.common.serialization.Deserializer;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serializer;

import java.nio.charset.StandardCharsets;
import java.util.Map;

public class NotificationSerde implements Serde<Notification> {

    private final ObjectMapper objectMapper;

    public NotificationSerde(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public Serializer<Notification> serializer() {
        return new Serializer<>() {

            @Override
            public byte[] serialize(String topic, Headers headers, Notification data) {
                try {
                    headers.add(
                            "__TypeId__",
                            Notification.class.getName().getBytes(StandardCharsets.UTF_8)
                    );
                    return objectMapper.writeValueAsBytes(data);
                } catch (Exception e) {
                    throw new RuntimeException("Notification serialization error", e);
                }
            }

            @Override
            public byte[] serialize(String topic, Notification data) {
                return serialize(topic, new RecordHeaders(), data);
            }
        };
    }

    @Override
    public Deserializer<Notification> deserializer() {
        return new Deserializer<>() {
            @Override
            public Notification deserialize(String topic, byte[] data) {
                try {
                    return objectMapper.readValue(data, Notification.class);
                } catch (Exception e) {
                    throw new RuntimeException("KafkaEvent deserialization error", e);
                }
            }
        };
    }

    @Override
    public void configure(Map<String, ?> configs, boolean isKey) {}

    @Override
    public void close() {}
}
