package com.ecommerce.orderservice.domain.order.dto;

import com.ecommerce.orderservice.domain.order.entity.OrderStatus;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter @NoArgsConstructor
public class OrderDto {
    private Long id;
    private Long userId;
    private OrderStatus orderStatus;
    private DeliveryDto deliveryDto;
    private List<OrderLineDto> orderLineDtoList;

    @Builder
    public OrderDto(Long id, Long userId, OrderStatus orderStatus,
                    DeliveryDto deliveryDto, List<OrderLineDto> orderLineDtoList) {
        this.id = id;
        this.userId = userId;
        this.orderStatus = orderStatus;
        this.deliveryDto = deliveryDto;
        this.orderLineDtoList = orderLineDtoList;
    }
}
