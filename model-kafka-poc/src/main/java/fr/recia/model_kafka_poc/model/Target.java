package fr.recia.model_kafka_poc.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Target {
    private TargetType type;
    private List<String> ids;
}
