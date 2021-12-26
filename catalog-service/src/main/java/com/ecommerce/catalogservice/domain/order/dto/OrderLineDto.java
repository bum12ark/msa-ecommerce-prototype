package com.ecommerce.catalogservice.domain.order.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter @NoArgsConstructor
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
