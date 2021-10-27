package com.epam.esm.entity;

public class ErrorInfo {

    private final String errorMessage;
    private final Integer errorCode;

    public ErrorInfo(String errorMessage, Integer errorCode) {
        this.errorMessage = errorMessage;
        this.errorCode = errorCode;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public Integer getErrorCode() {
        return errorCode;
    }
}
