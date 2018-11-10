package br.com.broovie.brooviespringboot.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class ResourceNotFoundException extends RuntimeException {
    public ResourceNotFoundException(Class clazz, long code) {
        super(clazz.getName() + "  not found with code " + code);
    }

    public ResourceNotFoundException(String message) {
        super(message);
    }
}