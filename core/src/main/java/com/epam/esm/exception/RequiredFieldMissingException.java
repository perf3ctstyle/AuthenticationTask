package com.epam.esm.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.BAD_REQUEST)
public class RequiredFieldMissingException extends RuntimeException {

    private final String fieldName;

    public RequiredFieldMissingException(String message, String fieldName) {
        super(message);
        this.fieldName = fieldName;
    }

    public RequiredFieldMissingException(String message, Throwable cause, String fieldName) {
        super(message, cause);
        this.fieldName = fieldName;
    }

    public RequiredFieldMissingException(Throwable cause, String fieldName) {
        super(cause);
        this.fieldName = fieldName;
    }

    public String getFieldName() {
        return fieldName;
    }
}