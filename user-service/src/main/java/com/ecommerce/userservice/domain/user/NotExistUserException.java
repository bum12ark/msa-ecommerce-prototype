package com.ecommerce.userservice.domain.user;

import com.ecommerce.userservice.exception.CustomException;
import com.ecommerce.userservice.exception.ErrorEnum;

public class NotExistUserException extends CustomException {

    public NotExistUserException() {
        super(ErrorEnum.NOT_EXIST_USER);
    }
}
