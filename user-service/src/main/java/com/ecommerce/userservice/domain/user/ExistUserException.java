package com.ecommerce.userservice.domain.user;

import com.ecommerce.userservice.exception.CustomException;
import com.ecommerce.userservice.exception.ErrorEnum;

public class ExistUserException extends CustomException {

    public ExistUserException() {
        super(ErrorEnum.EXIST_USER);
    }
}
