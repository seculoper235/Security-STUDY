package com.example.demo.Exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class NoSuchException extends PeopleException {
    public NoSuchException() {
        super(ErrorCode.RESOURCE_NOT_FOUND);
    }
}
