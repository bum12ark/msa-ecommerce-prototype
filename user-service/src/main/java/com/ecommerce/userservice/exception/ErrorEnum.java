package com.ecommerce.userservice.exception;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter @AllArgsConstructor
@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum ErrorEnum {
    EXIST_USER(HttpStatus.CONFLICT, "Duplicated user Information"),
    NOT_EXIST_USER(HttpStatus.CONFLICT, "Not Exist User");

    private final HttpStatus httpStatus;
    private final String message;

    @Override
    public String toString() {
        return "ErrorEnum{" +
                "httpStatus=" + httpStatus +
                ", message='" + message + '\'' +
                '}';
    }
}
