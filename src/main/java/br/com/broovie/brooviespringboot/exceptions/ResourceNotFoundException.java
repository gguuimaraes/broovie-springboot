package br.com.broovie.brooviespringboot.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class ResourceNotFoundException extends RuntimeException {
    public ResourceNotFoundException(Class clazz, String parametro, Object valor) {
        super(clazz.getName() + "  não encontrado(a) com " + parametro + " = " + valor);
    }

    public ResourceNotFoundException(String message) {
        super(message);
    }
}