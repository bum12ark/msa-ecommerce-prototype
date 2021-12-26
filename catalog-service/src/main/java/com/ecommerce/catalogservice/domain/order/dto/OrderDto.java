package com.ecommerce.catalogservice.domain.order.dto;

import lombok.Data;

import java.util.List;

@Data
public class OrderDto {
    private Long userId;
    private DeliveryDto deliveryDto;
    private List<OrderLineDto> orderLineDtoList;
}
