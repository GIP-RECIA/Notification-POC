package fr.recia.model_kafka_poc.model;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.common.header.Headers;
import org.apache.kafka.common.header.internals.RecordHeaders;
import org.apache.kafka.common.serialization.Deserializer;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serializer;

import java.nio.charset.StandardCharsets;
import java.util.Map;

public class DeviceTokenSetSerde implements Serde<DeviceTokenSet> {

    private final ObjectMapper objectMapper;

    public DeviceTokenSetSerde(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public Serializer<DeviceTokenSet> serializer() {
        return new Serializer<>() {

            @Override
            public byte[] serialize(String topic, Headers headers, DeviceTokenSet data) {
                try {
                    headers.add(
                            "__TypeId__",
                            DeviceTokenSet.class.getName().getBytes(StandardCharsets.UTF_8)
                    );
                    return objectMapper.writeValueAsBytes(data);
                } catch (Exception e) {
                    throw new RuntimeException("DeviceTokenSet serialization error", e);
                }
            }

            @Override
            public byte[] serialize(String topic, DeviceTokenSet data) {
                return serialize(topic, new RecordHeaders(), data);
            }
        };
    }

    @Override
    public Deserializer<DeviceTokenSet> deserializer() {
        return new Deserializer<>() {
            @Override
            public DeviceTokenSet deserialize(String topic, byte[] data) {
                try {
                    return objectMapper.readValue(data, DeviceTokenSet.class);
                } catch (Exception e) {
                    throw new RuntimeException("DeviceTokenSet deserialization error", e);
                }
            }
        };
    }

    @Override
    public void configure(Map<String, ?> configs, boolean isKey) {}

    @Override
    public void close() {}
}
