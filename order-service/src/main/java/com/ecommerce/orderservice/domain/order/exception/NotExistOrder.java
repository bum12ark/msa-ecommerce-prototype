package com.ecommerce.orderservice.domain.order.exception;

import com.ecommerce.orderservice.global.exception.CustomException;
import com.ecommerce.orderservice.global.exception.ErrorEnum;

public class NotExistOrder extends CustomException {

    public NotExistOrder() {
        super(ErrorEnum.NOT_EXIST_ORDER);
    }
}
