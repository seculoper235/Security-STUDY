package com.example.demo.Exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class AuthFailException extends PeopleException {
    public AuthFailException() {
        super(ErrorCode.AUTH_FAIL);
    }
}
