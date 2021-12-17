package com.ecommerce.orderservice.domain.order.dto;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
public class OrderDto {

    private Long userId;
    private DeliveryDto deliveryDto;
    private List<OrderLineDto> orderLineDtoList;

    @Builder
    public OrderDto(Long userId, DeliveryDto deliveryDto, List<OrderLineDto> orderLineDtoList) {
        this.userId = userId;
        this.deliveryDto = deliveryDto;
        this.orderLineDtoList = orderLineDtoList;
    }
}
