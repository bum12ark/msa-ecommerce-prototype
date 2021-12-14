package com.ecommerce.orderservice.global.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(CustomException.class)
    public ResponseEntity<ErrorBody> customExceptionHandler(CustomException ce) {
        ErrorEnum errorEnum = ce.getErrorEnum();

        log.error("#################################################");
        log.error("## ErrorEnum: " + errorEnum);
        log.error("#################################################");

        ErrorBody errorBody = new ErrorBody(errorEnum.getMessage());
        return ResponseEntity.status(errorEnum.getHttpStatus()).body(errorBody);
    }

    @AllArgsConstructor
    @Getter
    static class ErrorBody {
        private String message;
    }

}
