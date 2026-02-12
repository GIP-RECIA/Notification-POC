package fr.recia.consumer_push_poc.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class ErrorInfo {

    @JsonProperty("@type")
    private String type;
    private String errorCode;

}
