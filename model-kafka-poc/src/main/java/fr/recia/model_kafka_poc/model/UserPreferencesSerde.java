package fr.recia.model_kafka_poc.model;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.common.header.Headers;
import org.apache.kafka.common.header.internals.RecordHeaders;
import org.apache.kafka.common.serialization.Deserializer;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serializer;

import java.nio.charset.StandardCharsets;
import java.util.Map;

public class UserPreferencesSerde implements Serde<UserPreferences> {

    private final ObjectMapper objectMapper;

    public UserPreferencesSerde(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public Serializer<UserPreferences> serializer() {
        return new Serializer<>() {

            @Override
            public byte[] serialize(String topic, Headers headers, UserPreferences data) {
                try {
                    headers.add(
                            "__TypeId__",
                            UserPreferences.class.getName().getBytes(StandardCharsets.UTF_8)
                    );
                    return objectMapper.writeValueAsBytes(data);
                } catch (Exception e) {
                    throw new RuntimeException("UserPreferences serialization error", e);
                }
            }

            @Override
            public byte[] serialize(String topic, UserPreferences data) {
                return serialize(topic, new RecordHeaders(), data);
            }
        };
    }

    @Override
    public Deserializer<UserPreferences> deserializer() {
        return (topic, data) -> {
            try {
                return objectMapper.readValue(data, UserPreferences.class);
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
