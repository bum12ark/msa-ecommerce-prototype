package com.ecommerce.catalogservice.domain.catalog.exception;

import com.ecommerce.catalogservice.global.exception.CustomException;
import com.ecommerce.catalogservice.global.exception.ErrorEnum;

public class NotExistCategoryException extends CustomException {

    public NotExistCategoryException() {
        super(ErrorEnum.NOT_EXIST_CATEGORY);
    }
}
