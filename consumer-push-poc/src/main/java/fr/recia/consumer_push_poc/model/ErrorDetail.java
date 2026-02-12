package fr.recia.consumer_push_poc.model;

import lombok.Data;

import java.util.List;

@Data
public class ErrorDetail {
    private int code;
    private String message;
    private String status;
    private List<ErrorInfo> details;
}
