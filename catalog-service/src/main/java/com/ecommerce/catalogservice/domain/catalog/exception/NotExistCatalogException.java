package com.ecommerce.catalogservice.domain.catalog.exception;

import com.ecommerce.catalogservice.global.exception.CustomException;
import com.ecommerce.catalogservice.global.exception.ErrorEnum;

public class NotExistCatalogException extends CustomException {

    public NotExistCatalogException() {
        super(ErrorEnum.NOT_EXIST_CATALOG);
    }
}
