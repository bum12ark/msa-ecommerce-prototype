package com.ecommerce.orderservice.domain.order.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
public class OrderLineDto {
    private Long catalogId;
    private Integer count;
    private Integer orderPrice;

    @Builder
    public OrderLineDto(Long catalogId, Integer count, Integer orderPrice) {
        this.catalogId = catalogId;
        this.count = count;
        this.orderPrice = orderPrice;
    }
}
