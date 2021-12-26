package com.ecommerce.orderservice.domain.order.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
public class OrderLineDto {
    private Long id;
    private Long catalogId;
    private Integer count;
    private Integer orderPrice;

    @Builder
    public OrderLineDto(Long id, Long catalogId, Integer count, Integer orderPrice) {
        this.id = id;
        this.catalogId = catalogId;
        this.count = count;
        this.orderPrice = orderPrice;
    }
}
