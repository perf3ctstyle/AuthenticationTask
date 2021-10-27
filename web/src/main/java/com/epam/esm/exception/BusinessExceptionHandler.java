package com.epam.esm.exception;

import com.epam.esm.constant.MessageSourceConstants;
import com.epam.esm.entity.ErrorInfo;
import com.epam.esm.exception.JwtAuthenticationException;
import com.epam.esm.util.ControllerUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import javax.naming.AuthenticationException;
import java.util.Locale;

@ControllerAdvice
public class BusinessExceptionHandler extends ResponseEntityExceptionHandler {

    private final MessageSource messageSource;

    private static final int INVALID_AUTHORIZATION_DATA_CODE = 40101;

    @Autowired
    public BusinessExceptionHandler(MessageSource messageSource) {
        this.messageSource = messageSource;
    }

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ErrorInfo> handleAuthenticationException(Locale locale) {
        return ControllerUtils.createResponseEntityWithSpecifiedErrorInfo(
                messageSource.getMessage(MessageSourceConstants.INVALID_AUTHORIZATION_DATA, null, locale),
                INVALID_AUTHORIZATION_DATA_CODE,
                HttpStatus.NOT_FOUND);
    }
}