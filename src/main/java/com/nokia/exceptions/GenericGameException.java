package com.nokia.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
public class GenericGameException extends RuntimeException {
    public GenericGameException() {
        super();
    }

    public GenericGameException(String message) {
        super(message);
    }
}
