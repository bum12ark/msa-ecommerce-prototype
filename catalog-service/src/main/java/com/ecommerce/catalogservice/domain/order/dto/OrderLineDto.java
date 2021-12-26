package com.ecommerce.catalogservice.domain.order.dto;

import lombok.Data;

@Data
public class OrderLineDto {
    private Long catalogId;
    private Integer count;
    private Integer orderPrice;
}
