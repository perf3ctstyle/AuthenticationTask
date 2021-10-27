package com.epam.esm.util;

import com.epam.esm.entity.ErrorInfo;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class ControllerUtils {

    private ControllerUtils() {
    }

    public static ResponseEntity<ErrorInfo> createResponseEntityWithSpecifiedErrorInfo(String message, Integer errorCode, HttpStatus httpStatus) {
        ErrorInfo errorInfo = new ErrorInfo(message, errorCode);
        return new ResponseEntity<>(errorInfo, httpStatus);
    }
}
