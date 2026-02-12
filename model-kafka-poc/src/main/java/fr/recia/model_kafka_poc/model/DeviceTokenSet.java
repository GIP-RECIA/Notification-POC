package fr.recia.model_kafka_poc.model;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.HashSet;

@EqualsAndHashCode(callSuper = true)
@Data
public class DeviceTokenSet extends HashSet<String> {
}
