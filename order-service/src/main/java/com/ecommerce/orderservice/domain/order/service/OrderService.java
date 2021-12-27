package com.ecommerce.orderservice.domain.order.service;

import com.ecommerce.orderservice.domain.order.dto.OrderDto;

public interface OrderService {
    OrderDto createOrder(OrderDto orderDto);
    OrderDto cancelOrder(Long orderId);
}
