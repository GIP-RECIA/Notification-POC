package fr.recia.notifications.consumer_push.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class ErrorDetail {
    private int code;
    private String message;
    private String status;
    private List<ErrorInfo> details;
}
