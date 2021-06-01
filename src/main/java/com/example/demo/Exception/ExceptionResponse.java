package com.example.demo.Exception;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
public class ExceptionResponse {
    private final int status;
    private final String error;
    private final String msg;
    private final LocalDateTime timestamp = LocalDateTime.now();

    @Builder
    public ExceptionResponse(int status, String error, String msg) {
        this.status = status;
        this.error = error;
        this.msg = msg;
    }
}
