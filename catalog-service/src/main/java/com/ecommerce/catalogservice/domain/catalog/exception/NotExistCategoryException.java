package com.ecommerce.catalogservice.domain.catalog.exception;

import com.ecommerce.catalogservice.exception.CustomException;
import com.ecommerce.catalogservice.exception.ErrorEnum;

public class NotExistCategoryException extends CustomException {

    public NotExistCategoryException() {
        super(ErrorEnum.NOT_EXIST_CATEGORY);
    }
}
