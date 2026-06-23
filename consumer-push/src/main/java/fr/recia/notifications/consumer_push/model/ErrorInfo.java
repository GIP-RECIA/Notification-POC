package fr.recia.notifications.consumer_push.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class ErrorInfo {

    @JsonProperty("@type")
    private String type;
    private String errorCode;

}
