package com.example.demo.Exception;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;

@RestController
@ControllerAdvice
public class PeopleExceptHandler {
    @ExceptionHandler(NoSuchException.class)
    public ResponseEntity<ExceptionResponse> notPeopleFound(NoSuchException e) {
        ErrorCode code = e.getErrorCode();

        ExceptionResponse response = ExceptionResponse.builder()
                .status(code.getHttpcode())
                .error(code.getMycode())
                .msg(code.getDescription())
                .build();

        return ResponseEntity.status(code.getHttpcode()).body(response);
    }

    @ExceptionHandler(AuthFailException.class)
    public ResponseEntity<ExceptionResponse> loginFail(AuthFailException e) {
        ErrorCode code = e.getErrorCode();

        ExceptionResponse response = ExceptionResponse.builder()
                .status(code.getHttpcode())
                .error(code.getMycode())
                .msg(code.getDescription())
                .build();

        return ResponseEntity.status(code.getHttpcode()).body(response);
    }

    @ExceptionHandler(AuthForbiddenException.class)
    public ResponseEntity<ExceptionResponse> authorityForbidden(AuthForbiddenException e) {
        ErrorCode code = e.getErrorCode();

        ExceptionResponse response = ExceptionResponse.builder()
                .status(code.getHttpcode())
                .error(code.getMycode())
                .msg(code.getDescription())
                .build();

        return ResponseEntity.status(code.getHttpcode()).body(response);
    }
}
