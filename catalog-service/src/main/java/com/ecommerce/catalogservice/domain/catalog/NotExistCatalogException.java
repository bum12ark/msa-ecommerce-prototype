package com.ecommerce.catalogservice.domain.catalog;

import com.ecommerce.catalogservice.exception.CustomException;
import com.ecommerce.catalogservice.exception.ErrorEnum;

public class NotExistCatalogException extends CustomException {

    public NotExistCatalogException() {
        super(ErrorEnum.NOT_EXIST_CATALOG);
    }
}
