package com.ecommerce.catalogservice.domain.order.dto;

import com.fasterxml.jackson.annotation.JsonFormat;

@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum OrderStatus {
    PLACED, CANCEL, ORDER
}
