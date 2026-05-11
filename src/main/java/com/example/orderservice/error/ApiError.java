package com.example.orderservice.error;

import java.time.LocalDateTime;

public class ApiError {

    private int status;
    private String message;

    public String getPath() {
        return path;
    }

    private String path;
    private LocalDateTime timestamp;

    public ApiError(int status, String message, String path) {
        this.status = status;
        this.message = message;
        this.path = path;
        this.timestamp = LocalDateTime.now();
    }

    public int getStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }
}
