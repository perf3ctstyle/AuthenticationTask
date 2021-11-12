package com.epam.esm.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.NOT_FOUND)
public class ResourceNotFoundException extends RuntimeException {

    private final String resourceName;

    public ResourceNotFoundException(String message, String resourceName) {
        super(message);
        this.resourceName = resourceName;
    }

    public ResourceNotFoundException(String message, Throwable cause, String resourceName) {
        super(message, cause);
        this.resourceName = resourceName;
    }

    public ResourceNotFoundException(Throwable cause, String resourceName) {
        super(cause);
        this.resourceName = resourceName;
    }

    public String getResourceName() {
        return resourceName;
    }
}
