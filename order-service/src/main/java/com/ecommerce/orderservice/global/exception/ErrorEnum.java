package com.ecommerce.orderservice.global.exception;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum ErrorEnum {
    NOT_EXIST_ORDER(HttpStatus.CONFLICT, "Not exist order");

    private final HttpStatus httpStatus;
    private final String message;
}
