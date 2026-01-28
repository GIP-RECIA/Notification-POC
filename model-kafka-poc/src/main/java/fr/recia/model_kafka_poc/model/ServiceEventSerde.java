package fr.recia.model_kafka_poc.model;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.common.header.Headers;
import org.apache.kafka.common.header.internals.RecordHeaders;
import org.apache.kafka.common.serialization.Deserializer;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serializer;

import java.nio.charset.StandardCharsets;
import java.util.Map;

public class ServiceEventSerde implements Serde<ServiceEvent> {

    private final ObjectMapper objectMapper;

    public ServiceEventSerde(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public Serializer<ServiceEvent> serializer() {
        return new Serializer<>() {

            @Override
            public byte[] serialize(String topic, Headers headers, ServiceEvent data) {
                try {
                    headers.add(
                            "__TypeId__",
                            ServiceEvent.class.getName().getBytes(StandardCharsets.UTF_8)
                    );
                    return objectMapper.writeValueAsBytes(data);
                } catch (Exception e) {
                    throw new RuntimeException("ServiceEvent serialization error", e);
                }
            }

            @Override
            public byte[] serialize(String topic, ServiceEvent data) {
                return serialize(topic, new RecordHeaders(), data);
            }
        };
    }

    @Override
    public Deserializer<ServiceEvent> deserializer() {
        return (topic, data) -> {
            try {
                return objectMapper.readValue(data, ServiceEvent.class);
            } catch (Exception e) {
                throw new RuntimeException("UserPreferences deserialization error", e);
            }
        };
    }

    @Override
    public void configure(Map<String, ?> configs, boolean isKey) {}

    @Override
    public void close() {}
}
