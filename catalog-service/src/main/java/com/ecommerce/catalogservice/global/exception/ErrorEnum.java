package com.ecommerce.catalogservice.global.exception;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter @AllArgsConstructor
@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum ErrorEnum {
    NOT_EXIST_CATEGORY(HttpStatus.CONFLICT, "Not exist category"),
    NOT_EXIST_CATALOG(HttpStatus.CONFLICT, "Not exist catalog");

    private final HttpStatus httpStatus;
    private final String message;
}
