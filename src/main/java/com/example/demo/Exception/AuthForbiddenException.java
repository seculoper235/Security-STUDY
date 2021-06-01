package com.example.demo.Exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.FORBIDDEN)
public class AuthForbiddenException extends PeopleException {
    public AuthForbiddenException() {
        super(ErrorCode.AUTH_FORBIDDEN);
    }
}
