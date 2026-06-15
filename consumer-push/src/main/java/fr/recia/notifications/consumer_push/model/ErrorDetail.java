package fr.recia.notifications.consumer_push.model;

import lombok.Data;

import java.util.List;

@Data
public class ErrorDetail {
    private int code;
    private String message;
    private String status;
    private List<ErrorInfo> details;
}
